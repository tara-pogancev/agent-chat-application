package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
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
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);
	
	@Override
	public void register(User user) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "REGISTER");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		
		messageManager.post(message);
	}

	@Override
	public void login(User user) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "LOG_IN");
		message.userArgs.put("username", user.getUsername());
		message.userArgs.put("password", user.getPassword());
		
		messageManager.post(message);
	}

	@Override
	public void getloggedInUsers() {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "GET_LOGGEDIN");
		
		messageManager.post(message);
	}

	@Override
	public void sendMessageToAll(ChatMessage message) {	
		agentManager.startAgent(JNDILookup.ChatAgentLookup);
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", "chat");
		agentMsg.userArgs.put("command", "NEW_MESSAGE");
		agentMsg.userArgs.put("content", message.content);
		
		messageManager.post(agentMsg);
	}

}
