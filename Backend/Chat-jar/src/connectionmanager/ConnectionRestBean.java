package connectionmanager;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/connection")
public class ConnectionRestBean implements ConnectionRest {
	
	private Host localNode;
	private List<Host> nodeCluster = new ArrayList<>();
	private static final String HTTP_PREFIX = "http://";
	private static final String PORT_EXTENSION = ":8080/";
	private static final String MASTER_ALIAS = "master";
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private WSChat ws;	
	
	@PostConstruct
	private void init() {
		String address = getIpv4Address();
		String alias = "node" + nodeCluster.size();
		if (address.equals(getMasterNodeAddress())) {
			alias = "master";
		}
		
		localNode = new Host(alias, address);
		System.out.println("*** " + localNode.alias + " started at: " + localNode.address);
		
		if(!localNode.alias.equals(MASTER_ALIAS)) {
			// HANDSHAKE [0]
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(getMasterNodeAddress() + "Chat-war/rest/connection");
			ConnectionRest rest = rtarget.proxy(ConnectionRest.class);
			// HANDSHAKE [1] - New node notifies master
			nodeCluster = rest.registerNewNode(localNode);
			resteasyClient.close();
			System.out.println("*** Handshake successful. Number of connected nodes: " + nodeCluster.size());
		}
	}

	private String getIpv4Address() {
//		try {
//			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
//			System.out.println("***Local address: " + (String) mBeanServer.getAttribute(http, "boundAddress")); 
//			return (String) mBeanServer.getAttribute(http, "boundAddress");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}	
		
		try(final DatagramSocket socket = new DatagramSocket()){
			  socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			  String ip = socket.getLocalAddress().getHostAddress();
			  System.out.println("***IP address: " + ip);
			  return HTTP_PREFIX + ip + PORT_EXTENSION;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
	}
		
	private String getMasterNodeAddress() {
		try {
			InputStream fileInput  = ConnectionRestBean.class.getClassLoader().getResourceAsStream("../preferences/connection.properties");
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			return HTTP_PREFIX + connectionProperties.getProperty("master_node") + PORT_EXTENSION; 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@PreDestroy
	private void shutDown() {
//		instructNodesToDeleteNode(localNode.getAlias());
	}	
	
	@Schedule(hour = "*", minute="*", second="*/30")
	private void heartbeat() {
		System.out.println("*** Heartbeat protocol initiated");
//		for(String node : connectedNodes) {
//			System.out.println("Pinging node with alias: " + node);
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					boolean pingSuccessful = pingNode(node);
//					if(!pingSuccessful) {
//						System.out.println("Node with alias: " + node + " not alive. Deleting..");
//						connectedNodes.remove(node);
//						instructNodesToDeleteNode(node);
//					}
//				}
//			}).start();;
//		}
	}
	
	@Override
	public List<Host> registerNewNode(Host node) {
		// MASTER NODE FUNCTION
		// HANDSHAKE [1] - New node notifies master
		System.out.println("** Registering new node: " + node.alias);
		nodeCluster.add(node);
		for (Host host: nodeCluster) {
			// HANDSHAKE [2] - Master notifies others
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(host.getAddress() + "Chat-war/rest/connection");
			ConnectionRest rest = rtarget.proxy(ConnectionRest.class);
			rest.addNewNode(node);
			resteasyClient.close();
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// HANDSHAKE [4] - Master returns all other logged in users
				for (Host tempNode: nodeCluster) {					
					ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
					ResteasyWebTarget rtarget = resteasyClient.target(tempNode.getAddress() + "Chat-war/rest/connection");
					ConnectionRest rest = rtarget.proxy(ConnectionRest.class);
					rest.syncLoggedIn(node);
					resteasyClient.close();
				}
			}
		}).start();
		

		// HANDSHAKE [3] - Master returns all nodes
		return getNodes();
	}

	@Override
	public List<Host> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void syncLoggedIn(Host targetNode) {
		if (!targetNode.alias.equals(localNode.alias)) {
			ResteasyClient resteasyClient = new ResteasyClientBuilder().build();
			ResteasyWebTarget rtarget = resteasyClient.target(targetNode + "Chat-war/rest/connection");
			ConnectionRest rest = rtarget.proxy(ConnectionRest.class);
			rest.addLoggedInList(chatManager.getActiveUsers());
			resteasyClient.close();
		}
	}

	@Override
	public void deleteNode(String alias) {
		System.out.println("** Removing new node: " + alias);
		nodeCluster.removeIf(n -> n.getAlias().equals(alias));
//		agm.deleteRunningAgents(alias);
//		agm.deleteAgentTypes(alias);
	}

	@Override
	public void addNewNode(Host node) {
		System.out.println("** Adding new node: " + node.alias);
		nodeCluster.add(node);
	}

	@Override
	public boolean ping() {
		return true;
	}

	@Override
	public void addLoggedInList(List<User> activeUsers) {
		for (User user : activeUsers) {
			chatManager.addFromRemoteActive(user, localNode);
		}		
	}

}
