package chatmanager;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;

import models.User;

// TODO Implement the rest of Client-Server functionalities 
/**
 * Session Bean implementation class ChatBean
 */
@Stateful
@LocalBean
public class ChatManagerBean implements ChatManagerRemote, ChatManagerLocal {

	private List<User> registered = new ArrayList<User>();
	private List<User> loggedIn = new ArrayList<User>();
	
	/**
	 * Default constructor.
	 */
	public ChatManagerBean() {
	}

	@Override
	public boolean register(User user) {
		registered.add(user);
		return true;
	}

	@Override
	public boolean login(String username, String password) {
		boolean exists = registered.stream().anyMatch(u->u.getUsername().equals(username) && u.getPassword().equals(password));
		if(exists)
			loggedIn.add(new User(username, password));
		return exists;
	}

	@Override
	public List<User> loggedInUsers() {
		return loggedIn;
	}

}
