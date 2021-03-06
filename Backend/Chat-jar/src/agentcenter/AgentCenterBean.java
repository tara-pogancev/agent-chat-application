package agentcenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.jms.Message;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import models.ChatMessage;
import models.Host;
import models.User;
import ws.WSChat;

@Singleton
@Startup
@Remote(AgentCenter.class)
@Path("/connection")
public class AgentCenterBean implements AgentCenter {
	
	private Host localNode;
	private List<String> nodeCluster = new ArrayList<>();
	
	private static final String HTTP_PREFIX = "http://";
	private static final String PORT_EXTENSION = ":8080";
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private WSChat ws;		

	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	public MessageManagerRemote messageManager;
	
	@PostConstruct
	private void init() {
		String address = getNodeAddress();
		String alias = getNodeAlias() + PORT_EXTENSION;
		
		localNode = new Host(alias, address);
		System.out.println("*** " + localNode.alias + " started at: " + localNode.address);
		
		if(getMasterAlias() != null && !getMasterAlias().equals(PORT_EXTENSION)) {
			// HANDSHAKE [0]
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + getMasterAlias() + "/Chat-war/api/connection");
			AgentCenter rest = rtarget.proxy(AgentCenter.class);

			// HANDSHAKE [1] - New node notifies master
			nodeCluster = rest.registerNewNode(localNode.getAlias());	
			
			// HANDSHAKE [2] - Master requests list of performatives
			rest.getAgentClassesFromRemote(performativeEnumToString());

			nodeCluster.add(getMasterAlias());
			nodeCluster.removeIf(n -> n.equals(localNode.getAlias()));
			resteasyClient.close();
			System.out.println("\n*** Handshake successful. Number of connected nodes: " + nodeCluster.size() + "\n");
		}
	}

	private String getNodeAddress() {		
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			return (String) mBeanServer.getAttribute(http, "boundAddress");			
		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException | ReflectionException | MBeanException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getNodeAlias() {		
		return System.getProperty("jboss.node.name");
	}
		
	public String getMasterAlias() {
		try {
			InputStream fileInput  = AgentCenterBean.class.getClassLoader().getResourceAsStream("../preferences/connection.properties");
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			return connectionProperties.getProperty("master_node") + PORT_EXTENSION; 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PreDestroy
	private void shutDown() {
		notifyNodesShutDown(localNode.getAlias());
	}	
	
	private void notifyNodesShutDown(String alias) {
		for (String node: nodeCluster) {
			ResteasyClient client = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = client.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
			AgentCenter rest = rtarget.proxy(AgentCenter.class);
			rest.deleteNode(alias);
			client.close();
		}
	}

	@Override
	public void deleteNode(String alias) {
		System.out.println("*** Removing node: " + alias);
		nodeCluster.removeIf(n -> n.equals(alias));
		chatManager.logOutFromNode(alias);
		cachedAgents.removeFromNode(alias);
	}
	
	@Schedule(hour = "*", minute="*/1", persistent=false)
	private void heartbeat() {
		System.out.println("*** Heartbeat protocol initiated");
		for(String node : nodeCluster) {
			System.out.println("*** Pinging node with alias: " + node);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
						ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
						AgentCenter rest = rtarget.proxy(AgentCenter.class);
						rest.ping();
						System.out.println("*** Node: " + node + " is well and alive.");
						resteasyClient.close();
					} catch (Exception e) {
						System.out.println("*** Node: " + node + " didn't respond. Trying again.");
						try {
							ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
							ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
							AgentCenter rest = rtarget.proxy(AgentCenter.class);
							rest.ping();
							System.out.println("*** Node: " + node + " is well and alive.");
							resteasyClient.close();
						} catch (Exception e1) {
							System.out.println("*** Node: " + node + " died. Notifying other nodes.");
							nodeCluster.remove(node);
							notifyNodesShutDown(node);
						}												
					}
				}				
			}).start();
		}
	}
	
	@Override
	public List<String> registerNewNode(String nodeAlias) {
		// MASTER NODE FUNCTION
		// HANDSHAKE [1] - New node notifies master
		System.out.println("** Registering new node: " + nodeAlias);
		addNewNode(nodeAlias);
		for (String tempNode: nodeCluster) {
			// HANDSHAKE [3] - Master notifies others
			if (!tempNode.equals(nodeAlias)) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + tempNode + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.addNewNode(nodeAlias);
				resteasyClient.close();
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// HANDSHAKE [5] - Master returns all other logged in users and agents to the baby node
				// Master should already have the complete list!
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + nodeAlias + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				for (String username : chatManager.getActiveUsernames()) {
					User userToAdd = new User(username, localNode.getAlias());
					rest.addLoggedInFromRemote(userToAdd);
				}
				
				for (User user : chatManager.getLoggedInRemote()) {
					rest.addLoggedInFromRemote(user);
				}
				
				for (Agent agent: cachedAgents.getRunningAgents()) {
					rest.addAgentFromRemote(agent.getAgentId());
				}
				resteasyClient.close();
			}
		}).start();
		
		// HANDSHAKE [3] - Master returns all nodes
		return getNodes();
	}

	@Override
	public List<String> getNodes() {
		return nodeCluster;
	}

	@Override
	public void addNewNode(String nodeAlias) {
		if (!nodeAlias.equals(localNode.getAlias())) {
			System.out.println("** Adding new node: " + nodeAlias);
			nodeCluster.add(nodeAlias);
		}
	}

	@Override
	public boolean ping() {
		System.out.println("*** PINGED!");
		System.out.println("*** Just to let you know I have: " + chatManager.getActiveUsernames().size() + " local users, and "
				+ chatManager.getLoggedInRemote().size() + " remote users active.");
		return true;
	}

	@Override
	public void addLoggedInFromRemote(User user) {
		chatManager.addLoggedInFromRemote(user);
	}

	@Override
	public void removeLoggedInFromRemote(String user) {
		chatManager.removeFromRemoteActive(user);
	}

	@Override
	public void addChatMessageFromRemote(ChatMessage msg) {
		chatManager.saveNewMessageFromRemote(msg);
	}

	@Override
	public void notifyAllNewMessage(ChatMessage msg) {
		for (String node: nodeCluster) {
			if (!node.equals(localNode.getAlias())) {
				System.out.println("*** Forwarding new message to server from " + msg.getSender());
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.addChatMessageFromRemote(msg);
				resteasyClient.close();
			}
		}		
	}

	@Override
	public void notifyAllNewLogin(String user) {
		User userToAdd = new User(user, localNode.getAlias());
		for (String node: nodeCluster) {
			if (!node.equals(localNode.getAlias())) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.addLoggedInFromRemote(userToAdd);
				resteasyClient.close();
			}
		}		
	}

	@Override
	public void notifyAllLogout(String user) {
		for (String node: nodeCluster) {
			if (!node.equals(localNode.getAlias())) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.removeLoggedInFromRemote(user);
				resteasyClient.close();
			}
		}	
	}

	@Override
	public Host getHost() {
		return localNode;
	}


	// AGENTS
	@Override
	public void addAgentFromRemote(AgentId agentId) {
		System.out.println("Adding agent from remote: " + agentId.getName());
		cachedAgents.addRunningAgentFromRemote(agentId);		
	}
	

	@Override
	public void removeAgentFromRemote(AgentId agentId) {
		cachedAgents.stopAgentFromRemote(agentId);		
	}

	@Override
	public void getAgentClassesFromRemote(String values) {		
		System.out.println("I've been notified that newly registered node accepts the following performatives:");
		System.out.println(values);
	}

	@Override
	public String sendLocalAgentClasses() {
		return performativeEnumToString();
	}

	@Override
	public void notifyAllNewAgent(AgentId agentId) {
		for (String node: nodeCluster) {
			if (!node.equals(localNode.getAlias())) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.addAgentFromRemote(agentId);
				resteasyClient.close();
			}
		}			
	}

	@Override
	public void notifyAllAgentQuit(AgentId agentId) {
		for (String node: nodeCluster) {
			if (!node.equals(localNode.getAlias())) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
				AgentCenter rest = rtarget.proxy(AgentCenter.class);
				rest.removeAgentFromRemote(agentId);
				resteasyClient.close();
			}
		}	
	}
	
	private String performativeEnumToString() {
		String retVal = "";
		for (int i = 0; i < PerformativeEnum.values().length; i++) {
			if (i != 0) {
				retVal+=(", ");
			}
			retVal+=(PerformativeEnum.values()[i].toString());
		} 
		return retVal;
	}

	@Override
	public void forwardMessage(ACLMessage agentMessageToForward) {		
		System.out.println("Forwarding message to: " + agentMessageToForward.getRecievers().get(0).getHost().getAlias());
		String forwardAlias = agentMessageToForward.getRecievers().get(0).getHost().getAlias();
		if (forwardAlias != null && forwardAlias != "") {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + agentMessageToForward.getRecievers().get(0).getHost().getAlias() + "/Chat-war/api/connection");
			AgentCenter rest = rtarget.proxy(AgentCenter.class);
			rest.acceptMessage(agentMessageToForward);
			resteasyClient.close();
		}
	}

	@Override
	public void acceptMessage(ACLMessage agentMessageToForward) {
		System.out.println("Recieving forwarded agent message.");
		messageManager.post(agentMessageToForward);		
	}

}
