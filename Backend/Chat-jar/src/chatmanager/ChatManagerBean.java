package chatmanager;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import connectionmanager.ConnectionManager;
import connectionmanager.ConnectionManagerBean;
import models.ChatMessage;
import models.Host;
import models.User;
import ws.WSChat;

/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote {

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	private List<User> loggedInRemote = new ArrayList<User>();	// Password acts as a HOST field
	private List<ChatMessage> messages = new ArrayList<>();
	
	@EJB
	private WSChat ws;
	
	@EJB
	private ConnectionManager connectionManager;
	
	@Override
	public boolean register(User user) {
		if (registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername())) || user.username.equals("SYSTEM_AGENT")) {
			return false;
		} else {
			registered.add(user);			
			return true;
		}
	}
	
	@Override
	public String login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {			
			if (!isUserActive(username))  {
				loggedIn.add(getRegisteredByUsername(username));
				ws.notifyNewLogin(username);
				sendLoginToNetwork(username);	
				return "ok";
			}
			return "error";
		}
		return "invalid";
	}
	
	private User getRegisteredByUsername(String username) {
		for (User user : registered) {
			if (user.username.equals(username)) {
				return user;
			}
		} 		
		return null;
	}
	
	@Override
	public List<User> getActiveUsers() {
		return loggedIn;
	}


	@Override
	public List<String> getActiveUsernames() {
		List<String> usernames = new ArrayList<>();
		for (User user: loggedIn) {
			usernames.add(user.username);
		}
		return usernames;
	}
	
	private boolean isUserActive(String username) {
		for (String activeUsername : getActiveUsernames()) {
			if (activeUsername.equals(username)) {
				return true;
			}
		}		
		
		for (User activeUser : getLoggedInRemote()) {
			if (activeUser.username.equals(username)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<String> getRegisteredUsernames() {
		List<String> retVal = new ArrayList<>();
		for (User user: registered) {
			retVal.add(user.username);
		}
		return retVal;
	}

	@Override
	public List<ChatMessage> getMessagesByUser(String username) {
		List<ChatMessage> retVal = new ArrayList<ChatMessage>();
		for (ChatMessage msg : messages) {
			if (msg.reciever.equals(username)) {
				retVal.add(msg);
			}
		}
		return retVal;
	}

	@Override
	public void saveNewMessage(ChatMessage chatMessage) {
		messages.add(chatMessage);			
		sendMessageToNetwork(chatMessage);	
	}	

	@Override
	public void saveNewMessageFromRemote(ChatMessage chatMessage) {
		System.out.println("Recieved message from remote.");
		messages.add(chatMessage);
		ws.sendMessage(chatMessage.reciever, chatMessage);	
	}	
	
	@Override
	public boolean logOut(String username) {
		if (isUserActive(username)) {
			loggedIn.removeIf(u -> u.username.equals(username));
			System.out.println("--- LOGOUT: " + username + " ---");
			sendLogoutToNetwork(username);
			return true;
		} else {
			return false;
		}		
	}
	
	@Override
	public void logOutFromNode(String alias) {
		for (User u: loggedInRemote) {
			if (u.password.equals(alias)) {
				ws.notifyLogOut(u.username);
			}
		}
		
		loggedInRemote.removeIf(u -> u.password.equals(alias));
	}

	@Override
	public void addLoggedInFromRemote(User user) {
		System.out.println("Remote user logged in: " + user.username);
		loggedInRemote.add(new User(user.username, user.password));
		ws.notifyNewLogin(user.username);
	}

	@Override
	public void removeFromRemoteActive(String username) {
		System.out.println("Remote user logged out: " + username);
		loggedInRemote.removeIf(u -> u.username.equals(username));		
		ws.notifyLogOut(username);
	}


	@Override
	public List<User> getLoggedInRemote() {
		return loggedInRemote;
	}

	@Override
	public void sendMessageToNetwork(ChatMessage chatMessage) {
		connectionManager.notifyAllNewMessage(chatMessage);
	}

	@Override
	public void sendLoginToNetwork(String username) {
		connectionManager.notifyAllNewLogin(username);		
	}

	@Override
	public void sendLogoutToNetwork(String username) {
		connectionManager.notifyAllLogout(username);			
	}
	
	private String getMasterAlias() {
		try {
			InputStream fileInput  = ConnectionManagerBean.class.getClassLoader().getResourceAsStream("../preferences/connection.properties");
			Properties connectionProperties = new Properties();
			connectionProperties.load(fileInput);
			fileInput.close();
			return connectionProperties.getProperty("master_node"); 
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Default constructor. Just inserts some basic data into the master node.
	 * @throws ParseException 
	 */
	public ChatManagerBean() throws ParseException {		
		if (getMasterAlias() == null || getMasterAlias().equals("")) {
			User u1 = new User ("tara", "123");
			User u2 = new User ("zack", "123");
			User u3 = new User ("sephiroth", "123");		
			registered.add(u1);
			registered.add(u2);
			registered.add(u3);			
			
			List<User> startingUsers = new ArrayList<>();
			startingUsers.add(u1);
			startingUsers.add(u2);
			startingUsers.add(u3);				
			
			ChatMessage c11 = new ChatMessage();
			c11.setReciever("zack");
			c11.setSender("tara");
			c11.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("02/02/2022"));
			c11.setSubject("Good Evening!");
			c11.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In elit justo, venenatis vel tincidunt ac, fermentum id est. Donec eget faucibus tellus. Aliquam erat volutpat. Nullam vitae sapien ut orci interdum venenatis. Praesent id varius velit, sed imperdiet risus. Nulla porttitor quam dolor. Curabitur eu mattis neque. Nunc consectetur, quam a cursus aliquet, enim nibh aliquet massa, eu vulputate dolor metus vitae orci.\r\n"  
					 );
			ChatMessage c12 = new ChatMessage();
			c12.setReciever("tara");
			c12.setSender("tara");
			c12.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("02/02/2022"));
			c12.setSubject("Good Evening!");
			c12.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In elit justo, venenatis vel tincidunt ac, fermentum id est. Donec eget faucibus tellus. Aliquam erat volutpat. Nullam vitae sapien ut orci interdum venenatis. Praesent id varius velit, sed imperdiet risus. Nulla porttitor quam dolor. Curabitur eu mattis neque. Nunc consectetur, quam a cursus aliquet, enim nibh aliquet massa, eu vulputate dolor metus vitae orci.\r\n"  
					 );
			ChatMessage c13 = new ChatMessage();
			c13.setReciever("sephiroth");
			c13.setSender("tara");
			c13.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("02/02/2022"));
			c13.setSubject("Good Evening!");
			c13.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In elit justo, venenatis vel tincidunt ac, fermentum id est. Donec eget faucibus tellus. Aliquam erat volutpat. Nullam vitae sapien ut orci interdum venenatis. Praesent id varius velit, sed imperdiet risus. Nulla porttitor quam dolor. Curabitur eu mattis neque. Nunc consectetur, quam a cursus aliquet, enim nibh aliquet massa, eu vulputate dolor metus vitae orci.\r\n"  
					 );
			ChatMessage c21 = new ChatMessage();
			c21.setReciever("sephiroth");
			c21.setSender("zack");
			c21.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/03/2022"));
			c21.setSubject("Good Morning!");
			c21.setContent("Nam sed posuere tellus. Praesent sit amet convallis orci. Mauris ipsum arcu, lobortis quis felis at, lacinia consequat ex. Nullam tempus enim a odio laoreet, sit amet auctor nisi mattis. Mauris tortor velit, egestas a leo sit amet, volutpat tristique ipsum. Cras eget vulputate nunc. "  
					);	
			ChatMessage c22 = new ChatMessage();
			c22.setReciever("tara");
			c22.setSender("zack");
			c22.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/03/2022"));
			c22.setSubject("Good Morning!");
			c22.setContent("Nam sed posuere tellus. Praesent sit amet convallis orci. Mauris ipsum arcu, lobortis quis felis at, lacinia consequat ex. Nullam tempus enim a odio laoreet, sit amet auctor nisi mattis. Mauris tortor velit, egestas a leo sit amet, volutpat tristique ipsum. Cras eget vulputate nunc. "  
					);	
			ChatMessage c23 = new ChatMessage();
			c23.setReciever("zack");
			c23.setSender("zack");
			c23.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/03/2022"));
			c23.setSubject("Good Morning!");
			c23.setContent("Nam sed posuere tellus. Praesent sit amet convallis orci. Mauris ipsum arcu, lobortis quis felis at, lacinia consequat ex. Nullam tempus enim a odio laoreet, sit amet auctor nisi mattis. Mauris tortor velit, egestas a leo sit amet, volutpat tristique ipsum. Cras eget vulputate nunc. "  
					);	
			ChatMessage c3 = new ChatMessage();
			c3.setReciever("zack");
			c3.setSender("sephiroth");
			c3.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2022"));
			c3.setSubject("How do you do?");
			c3.setContent("Ut imperdiet et risus eu luctus. Suspendisse vel ultrices tortor, in faucibus nulla. Proin aliquam rhoncus fringilla. Nulla enim ligula, maximus ac enim vitae, iaculis pulvinar turpis. Duis vulputate enim id ante pellentesque consectetur. Curabitur rutrum ex eu enim viverra sagittis. Sed scelerisque sollicitudin finibus. Fusce bibendum fringilla risus gravida sollicitudin"  
					);
			ChatMessage c31 = new ChatMessage();
			c31.setReciever("zack");
			c31.setSender("sephiroth");
			c31.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2022"));
			c31.setSubject("How do you do?");
			c31.setContent("Ut imperdiet et risus eu luctus. Suspendisse vel ultrices tortor, in faucibus nulla. Proin aliquam rhoncus fringilla. Nulla enim ligula, maximus ac enim vitae, iaculis pulvinar turpis. Duis vulputate enim id ante pellentesque consectetur. Curabitur rutrum ex eu enim viverra sagittis. Sed scelerisque sollicitudin finibus. Fusce bibendum fringilla risus gravida sollicitudin"  
					);
			ChatMessage c32 = new ChatMessage();
			c32.setReciever("sephiroth");
			c32.setSender("sephiroth");
			c32.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2022"));
			c32.setSubject("How do you do?");
			c32.setContent("Ut imperdiet et risus eu luctus. Suspendisse vel ultrices tortor, in faucibus nulla. Proin aliquam rhoncus fringilla. Nulla enim ligula, maximus ac enim vitae, iaculis pulvinar turpis. Duis vulputate enim id ante pellentesque consectetur. Curabitur rutrum ex eu enim viverra sagittis. Sed scelerisque sollicitudin finibus. Fusce bibendum fringilla risus gravida sollicitudin"  
					);
			ChatMessage c33 = new ChatMessage();
			c33.setReciever("tara");
			c33.setSender("sephiroth");
			c33.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2022"));
			c33.setSubject("How do you do?");
			c33.setContent("Ut imperdiet et risus eu luctus. Suspendisse vel ultrices tortor, in faucibus nulla. Proin aliquam rhoncus fringilla. Nulla enim ligula, maximus ac enim vitae, iaculis pulvinar turpis. Duis vulputate enim id ante pellentesque consectetur. Curabitur rutrum ex eu enim viverra sagittis. Sed scelerisque sollicitudin finibus. Fusce bibendum fringilla risus gravida sollicitudin"  
					);
			messages.add(c11);
			messages.add(c13);
			messages.add(c12);
			messages.add(c21);
			messages.add(c22);
			messages.add(c23);
			messages.add(c31);
			messages.add(c32);
			messages.add(c33);
			
			System.out.println("Master node data imported.");
		}
	}



}
