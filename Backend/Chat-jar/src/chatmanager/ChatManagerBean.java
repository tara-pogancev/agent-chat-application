package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import models.User;
import ws.WSChat;

/**
 * Session Bean implementation class ChatBean
 */
@Singleton
@LocalBean
public class ChatManagerBean implements ChatManagerRemote {
	
	//@EJB
	//private WSChat ws;

	private List<User> registered = new ArrayList<User>();
	private List<String> loggedIn = new ArrayList<String>();
	
	/**
	 * Default constructor.
	 */
	public ChatManagerBean() {
		User u1 = new User ("tara", "123", null);
		User u2 = new User ("zack", "123", null);
		User u3 = new User ("sephiroth", "123", null);		
		registered.add(u1);
		registered.add(u2);
		registered.add(u3);				
	}

	@Override
	public boolean register(User user) {
		if (registered.stream().anyMatch(u->u.getUsername().equals(user.getUsername()))) {
			return false;
		} else {
			registered.add(user);
			//ws.notifyNewRegistration(user.username);
			return true;
		}
	}

	@Override
	public boolean login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {			
			if (!isUserActive(username))  {
				loggedIn.add(username);
				//ws.notifyNewLogin(username);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> loggedInUsers() {
		return loggedIn;
	}

	@Override
	public void logOut(String username) {
		if (isUserActive(username)) {
			loggedIn.remove(username);
			//ws.closeSessionOnLogOut(username);
			System.out.println("LogOut - " + username + ".");
		}
		
	}
	
	private boolean isUserActive(String username) {
		for (String activeUsername : loggedIn) {
			if (activeUsername.equals(username)) {
				return true;
			}
		}
		
		return false;
	}

}
