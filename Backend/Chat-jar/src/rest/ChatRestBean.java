package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import models.User;
import util.JNDILookup;

@Stateless
@Path("/chat")
public class ChatRestBean implements ChatRest {

	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	
	@Override
	public boolean register(User user) {
		return chatManager.register(new User(user.username, user.password, null));
	}

	@Override
	public boolean login(User user) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "LOG_IN");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		
		messageManager.post(message);
		return true;
	}

	@Override
	public void getloggedInUsers() {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "GET_LOGGEDIN");
		
		messageManager.post(message);
	}

	@Override
	public void getregisteredUsers() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessageToAllActive(ChatMessage message) {
		agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, "tara");
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", "tara");
		agentMsg.userArgs.put("command", "NEW_MESSAGE");
		agentMsg.userArgs.put("content", message.getContent());
		
		messageManager.post(agentMsg);
		
	}

	@Override
	public void sendMessage(ChatMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getUsersMessages(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logOut(String username) {
		// TODO Auto-generated method stub
		
	}

}
