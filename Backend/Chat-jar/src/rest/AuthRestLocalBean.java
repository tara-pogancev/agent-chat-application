package rest;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import chatmanager.ChatManagerRemote;
import messagemanager.AgentMessage;
import messagemanager.MessageManagerRemote;
import models.User;
import util.JNDILookup;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/chat")
public class AuthRestLocalBean implements AuthRestLocal {
	
	@EJB
	public MessageManagerRemote messageManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);
	
	@Override
	public Response register(User user) {
		agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, "SYSTEM_AGENT");
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", "SYSTEM_AGENT");
		agentMsg.userArgs.put("command", "LOGOUT");
		agentMsg.userArgs.put("username", user.username);		
		
		boolean response = chatManager.register(new User(user.username, user.password, null));
		if (response) {
			System.out.println("--- REGISTER: " + user.username + " ---");
			messageManager.post(agentMsg);	
		}
		return Response.status(Status.OK).entity(response).build();
	}

	@Override
	public Response login(User user) {
		agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, "SYSTEM_AGENT");
		AgentMessage agentMsg = new AgentMessage();
		agentMsg.userArgs.put("receiver", "SYSTEM_AGENT");
		agentMsg.userArgs.put("command", "LOGOUT");
		agentMsg.userArgs.put("username", user.username);		
		
		boolean response = chatManager.login(user.username, user.password);
		if (response) {
			System.out.println("--- LOGIN: " + user.username + " ---");
			messageManager.post(agentMsg);	
		}
		return Response.status(Status.OK).entity(response).build();
	}

}
