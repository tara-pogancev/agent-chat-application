package rest;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import util.JNDILookup;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/chat")
public class ChatRestRemoteBean implements ChatRestRemote {

	@EJB
	public MessageManagerRemote messageManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	@Override
	public void getLoggedInUsers() {
		// TODO Auto-generated method stub		
	}

	@Override
	public void getRegisteredUsers() {
		// TODO Auto-generated method stub		
		
	}

	@Override
	public void sendMessageToAllActive(ChatMessage message) {
		agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, message.getSender());
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", message.getSender());
		agentMsg.userArgs.put("command", "NEW_GROUP_MESSAGE");
		agentMsg.userArgs.put("sender", message.getSender());		
		agentMsg.userArgs.put("subject", message.getSubject());
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
		agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "sys");
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", "sys");
		agentMsg.userArgs.put("command", "LOGOUT");
		agentMsg.userArgs.put("username", username);
		
		Boolean response = chatManager.logOut(username);	
		if (response) {
			messageManager.post(agentMsg);	
		}
	}

}
