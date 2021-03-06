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
import agents.Agent;
import agents.AgentTypeEnum;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
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
	public void getLoggedInUsers(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, username, AgentTypeEnum.CHAT_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.GET_ACTIVE_USERS);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);		
	}

	@Override
	public void getRegisteredUsers(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, username, AgentTypeEnum.CHAT_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.GET_REGISTERED_USERS);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);				
	}

	@Override
	public void sendMessageToAllActive(ChatMessage message) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, message.getSender(), AgentTypeEnum.CHAT_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.SEND_GROUP_MESSAGE);
		agentMsg.userArgs.put("target", message.getReciever());	
		agentMsg.userArgs.put("sender", message.getSender());		
		agentMsg.userArgs.put("subject", message.getSubject());
		agentMsg.userArgs.put("content", message.getContent());
		
		messageManager.post(agentMsg);		
	}

	@Override
	public void sendMessage(ChatMessage message) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, message.getSender(), AgentTypeEnum.CHAT_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.SEND_MESSAGE);
		agentMsg.userArgs.put("target", message.getReciever());	
		agentMsg.userArgs.put("sender", message.getSender());		
		agentMsg.userArgs.put("subject", message.getSubject());
		agentMsg.userArgs.put("content", message.getContent());
		
		messageManager.post(agentMsg);			
	}

	@Override
	public void getUsersMessages(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, username, AgentTypeEnum.CHAT_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.GET_MESSAGES);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);			
	}

	@Override
	public void logOut(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.AuthAgentLookup, "AUTH_AGENT", AgentTypeEnum.AUTH_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.LOGOUT);
		agentMsg.setContent(username);
		
		Boolean response = chatManager.logOut(username);	
		if (response) {
			System.out.println("--- LOGOUT: " + username + " ---");
			messageManager.post(agentMsg);	
		}
	}

}
