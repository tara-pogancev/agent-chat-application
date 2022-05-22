package connectionmanager;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import util.ResourceLoader;


import chatmanager.ChatManagerRemote;
import models.Host;
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
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private WSChat ws;	
	
	@PostConstruct
	private void init() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			System.out.println("***Local address: " + (String) mBeanServer.getAttribute(http, "boundAddress")); 
		} catch (Exception e) {
			e.printStackTrace();
		}
//		getLocalNodeInfo();
//		if(!localNode.isMaster())
//			handshake();
	}
	
	@PreDestroy
	private void shutDown() {
//		instructNodesToDeleteNode(localNode.getAlias());
	}	
	
	@Schedule(hour = "*", minute="*", second="*/30")
	private void heartbeat() {
		System.out.println("Heartbeat protocol initiated");
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
	public Response registerNewNode(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response notifyNewNode(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response getAllLoggedIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteNode(String alias) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response ping(String alias) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String getMasterNodeAlias() {
		try {
			InputStream fileInput  = ConnectionRestBean.class.getClassLoader().getResourceAsStream("../preferences/connection.properties");
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			return connectionProperties.getProperty("master_node"); 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	

}
