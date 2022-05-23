package chatmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

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
	private Map<String, User> loggedInRemote = new HashMap<String, User>();
	private List<ChatMessage> messages = new ArrayList<>();
	
	@EJB
	private WSChat ws;
	
	/**
	 * Default constructor.
	 * @throws ParseException 
	 */
	public ChatManagerBean() throws ParseException {		
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
		
		ChatMessage c1 = new ChatMessage();
		c1.setReciever(startingUsers);
		c1.setSender("tara");
		c1.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("02/02/2022"));
		c1.setSubject("Good Evening!");
		c1.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit. In elit justo, venenatis vel tincidunt ac, fermentum id est. Donec eget faucibus tellus. Aliquam erat volutpat. Nullam vitae sapien ut orci interdum venenatis. Praesent id varius velit, sed imperdiet risus. Nulla porttitor quam dolor. Curabitur eu mattis neque. Nunc consectetur, quam a cursus aliquet, enim nibh aliquet massa, eu vulputate dolor metus vitae orci.\r\n"  
				 );
		ChatMessage c2 = new ChatMessage();
		c2.setReciever(startingUsers);
		c2.setSender("zack");
		c2.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("03/03/2022"));
		c2.setSubject("Good Morning!");
		c2.setContent("Nam sed posuere tellus. Praesent sit amet convallis orci. Mauris ipsum arcu, lobortis quis felis at, lacinia consequat ex. Nullam tempus enim a odio laoreet, sit amet auctor nisi mattis. Mauris tortor velit, egestas a leo sit amet, volutpat tristique ipsum. Cras eget vulputate nunc. "  
				);		
		ChatMessage c3 = new ChatMessage();
		c3.setReciever(startingUsers);
		c3.setSender("sephiroth");
		c3.setDate(new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2022"));
		c3.setSubject("How do you do?");
		c3.setContent("Ut imperdiet et risus eu luctus. Suspendisse vel ultrices tortor, in faucibus nulla. Proin aliquam rhoncus fringilla. Nulla enim ligula, maximus ac enim vitae, iaculis pulvinar turpis. Duis vulputate enim id ante pellentesque consectetur. Curabitur rutrum ex eu enim viverra sagittis. Sed scelerisque sollicitudin finibus. Fusce bibendum fringilla risus gravida sollicitudin"  
				);
		messages.add(c1);
		messages.add(c2);
		messages.add(c3);
	}

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
	public boolean login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {			
			if (!isUserActive(username))  {
				loggedIn.add(getRegisteredByUsername(username));
				return true;
			}
		}
		return false;
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
	public List<String> getActiveUsernames() {
		List<String> usernames = new ArrayList<>();
		for (User user: loggedIn) {
			usernames.add(user.username);
		}
		return usernames;
	}
	
	@Override
	public List<User> getActiveUsers() {
		return loggedIn;
	}

	@Override
	public boolean logOut(String username) {
		if (isUserActive(username)) {
			loggedIn.removeIf(u -> u.username.equals(username));
			System.out.println("--- LOGOUT: " + username + " ---");
			return true;
		} else {
			return false;
		}		
	}
	
	private boolean isUserActive(String username) {
		for (String activeUsername : getActiveUsernames()) {
			if (activeUsername.equals(username)) {
				return true;
			}
		}		
		return false;
	}

	@Override
	public List<String> getRegisteredUsers() {
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
			if (msg.recievers.stream().anyMatch(u -> u.getUsername().equals(username))) {
				retVal.add(msg);
			}
		}
		return retVal;
	}

	@Override
	public void saveNewMessage(ChatMessage chatMessage) {
		messages.add(chatMessage);
		
	}

	@Override
	public void saveNewMessage(ChatMessage chatMessage, String groupReceiver) {
		User receiver = registered.stream().filter(u->u.getUsername().equals(groupReceiver)).findFirst().orElse(null);
		if (receiver != null) {
			chatMessage.recievers.add(receiver);
			messages.add(chatMessage);
		}		
	}

	@Override
	public void forceLogout(String username) {
		if (isUserActive(username)) {
			loggedIn.removeIf(u -> u.username.equals(username));
			System.out.println("--- LOGOUT: " + username + " ---");
		}	
	}

	@Override
	public void addFromRemoteActive(User user, Host host) {
		loggedInRemote.put(host.alias, user);
		ws.notifyNewLogin(user.username);
	}

	@Override
	public void addFromRemoteRegistered(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeFromRemoteActive(User user) {
		// TODO Auto-generated method stub
		
	}

}
