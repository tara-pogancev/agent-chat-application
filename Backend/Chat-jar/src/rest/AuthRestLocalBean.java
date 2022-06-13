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
import agents.Agent;
import agents.AgentTypeEnum;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
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
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.AuthAgentLookup, "AUTH_AGENT", AgentTypeEnum.AUTH_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.REGISTER);
		agentMsg.setContent(user.username);	
		
		boolean response = chatManager.register(new User(user.username, user.password));
		if (response) {
			System.out.println("--- REGISTER: " + user.username + " ---");
			messageManager.post(agentMsg);	
		}
		return Response.status(Status.OK).entity(response).build();
	}

	@Override
	public Response login(User user) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.AuthAgentLookup, "AUTH_AGENT", AgentTypeEnum.AUTH_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());
		agentMsg.setPerformative(PerformativeEnum.LOGIN);
		agentMsg.setContent(user.username);		
		
		String response = chatManager.login(user.username, user.password);
		if (response.equals("ok")) {
			System.out.println("--- LOGIN: " + user.username + " ---");
			messageManager.post(agentMsg);	
			return Response.status(Status.OK).entity(response).build();
		} else if (response.equals("invalid")) {
			return Response.status(Status.NOT_FOUND).entity(response).build();
		} else {
			return Response.status(Status.CONFLICT).entity(response).build();
		}
	}

}
