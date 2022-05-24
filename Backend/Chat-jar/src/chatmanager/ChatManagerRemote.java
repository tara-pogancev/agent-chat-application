package chatmanager;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import models.ChatMessage;
import models.Host;
import models.User;

@Remote
public interface ChatManagerRemote {

	public String login(String username, String password);	

	public void addLoggedInFromRemote(User user);
		
	public void removeFromRemoteActive(String username);

	public boolean register(User user);

	public List<String> getActiveUsernames();	

	public List<String> getRegisteredUsernames();

	public boolean logOut(String username);
	
	public List<ChatMessage> getMessagesByUser(String username);
	
	public void saveNewMessageFromRemote(ChatMessage chatMessage);
	
	public void saveNewMessage(ChatMessage chatMessage);

	public List<User> getActiveUsers();

	public void logOutFromNode(String alias);
	
	public List<User> getLoggedInRemote();
	
	public void sendMessageToNetwork(ChatMessage chatMessage);
	
	public void sendLoginToNetwork(String username);
	
	public void sendLogoutToNetwork(String username);

}
