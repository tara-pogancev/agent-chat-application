package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import models.User;
import ws.WSChat;

/**
 * Session Bean implementation class ChatBean
 */
@Stateful
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal {
	
	@EJB
	private WSChat ws;

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
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
			System.out.println("New user registered: " + user.username);
			ws.notifyNewRegistration(user.username);
			return true;
		}
	}

	@Override
	public boolean login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists) {
			boolean isActive = loggedIn.stream().anyMatch(u->u.getUsername().equals(username));
			if (!isActive) {
				loggedIn.add(new User(username, password, null));
				ws.notifyNewLogin(username);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<User> loggedInUsers() {
		return loggedIn;
	}

}
