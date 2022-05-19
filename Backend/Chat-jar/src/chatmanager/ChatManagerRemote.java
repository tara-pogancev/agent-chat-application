package chatmanager;

import java.util.List;

import javax.ejb.Remote;

import models.ChatMessage;
import models.User;

@Remote
public interface ChatManagerRemote {

	public boolean login(String username, String password);

	public boolean register(User user);

	public List<String> getActiveUsers();	

	public List<String> getRegisteredUsers();

	public boolean logOut(String username);
	
	public List<ChatMessage> getMessagesByUser(String username);
	
	public void saveNewMessage(ChatMessage chatMessage);

	public void saveNewMessage(ChatMessage chatMessage, String groupReceiver);	

}
