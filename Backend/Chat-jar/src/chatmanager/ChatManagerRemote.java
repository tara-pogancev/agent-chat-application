package chatmanager;

import java.util.List;

import javax.ejb.Remote;

import models.ChatMessage;
import models.Host;
import models.User;

@Remote
public interface ChatManagerRemote {

	public boolean login(String username, String password);	

	public void addFromRemoteActive(User user, Host host);
	
	public void addFromRemoteRegistered(User user);
	
	public void removeFromRemoteActive(User user);

	public boolean register(User user);

	public List<String> getActiveUsernames();	

	public List<String> getRegisteredUsers();

	public boolean logOut(String username);
	
	public List<ChatMessage> getMessagesByUser(String username);
	
	public void saveNewMessage(ChatMessage chatMessage);

	public void saveNewMessage(ChatMessage chatMessage, String groupReceiver);

	public void forceLogout(String username);

	public List<User> getActiveUsers();

	public void logOutFromNode(String alias);


}
