package connectionmanager;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import chatmanager.ChatManagerRemote;
import models.Host;
import models.User;
import ws.WSChat;

@Singleton
@Startup
@Remote(ConnectionManager.class)
@Path("/connection")
public class ConnectionManagerBean implements ConnectionManager {
	
	private Host localNode;
	private List<String> nodeCluster = new ArrayList<>();
	
	private static final String HTTP_PREFIX = "http://";
	private static final String PORT_EXTENSION = ":8080";
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private WSChat ws;	
	
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
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			// HANDSHAKE [1] - New node notifies master
			nodeCluster = rest.registerNewNode(localNode.getAlias());
			nodeCluster.add(getMasterAlias());
			nodeCluster.removeIf(n -> n.equals(localNode.getAlias()));
			resteasyClient.close();
			System.out.println("*** Handshake successful. Number of connected nodes: " + nodeCluster.size());
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
		
	private String getMasterAlias() {
		try {
			InputStream fileInput  = ConnectionManagerBean.class.getClassLoader().getResourceAsStream("../preferences/connection.properties");
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
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.deleteNode(alias);
			client.close();
		}
	}

	@Override
	public void deleteNode(String alias) {
		System.out.println("*** Removing node: " + alias);
		nodeCluster.removeIf(n -> n.equals(alias));
		chatManager.logOutFromNode(alias);
	}
	
	@Schedule(hour = "*", minute="*/2", persistent=false)
	private void heartbeat() {
		System.out.println("*** Heartbeat protocol initiated");
		for(String node : nodeCluster) {
			System.out.println("Pinging node with alias: " + node);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
						ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + node + "/Chat-war/api/connection");
						ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
						boolean ping = rest.ping();
						System.out.println("Node: " + node + " is well and alive.");
						resteasyClient.close();
					} catch (Exception e) {
						System.out.println("Node: " + node + " died. Notifying other nodes.");
						nodeCluster.remove(node);
						notifyNodesShutDown(node);
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
		nodeCluster.add(nodeAlias);
		for (String tempNode: nodeCluster) {
			// HANDSHAKE [2] - Master notifies others
			if (!tempNode.equals(nodeAlias)) {
				ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + tempNode + "/Chat-war/api/connection");
				ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
				rest.addNewNode(nodeAlias);
				resteasyClient.close();
			}
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// HANDSHAKE [4] - Master returns all other logged in users to the baby node
				
//				for (String tempNode: nodeCluster) {					
//					ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
//					ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + tempNode + "/Chat-war/api/connection");
//					ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
//					rest.syncLoggedIn(node);
//					resteasyClient.close();
//				}
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
	public void syncLoggedIn(String targetNode) {
		if (!targetNode.equals(localNode.alias)) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(HTTP_PREFIX + targetNode + "/Chat-war/api/connection");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.addLoggedInList(chatManager.getActiveUsers());
			resteasyClient.close();
		}
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
		System.out.println(localNode.getAlias() + ": PING!");
		return true;
	}

	@Override
	public void addLoggedInList(List<User> activeUsers) {
		for (User user : activeUsers) {
			chatManager.addFromRemoteActive(user, localNode);
		}		
	}
}
