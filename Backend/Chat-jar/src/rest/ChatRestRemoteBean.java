package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
public class ChatRestRemoteBean implements ChatRestRemote {

	@EJB
	private MessageManagerRemote messageManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

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
		System.out.println("hello");
		agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, "tara");
		System.out.println("hello");
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