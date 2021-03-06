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
import agents.AgentId;
import agents.AgentTypeEnum;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import util.JNDILookup;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/system")
public class SystemRestRemoteBean implements SystemRestRemote {	

	@EJB
	public MessageManagerRemote messageManager;
	
	@EJB
	private ChatManagerRemote chatManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	@Override
	public void getAgentTypes(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "SYSTEM_AGENT", AgentTypeEnum.SYSTEM_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.GET_AGENT_TYPES);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);			
	}

	@Override
	public void getRunningAgents(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "SYSTEM_AGENT", AgentTypeEnum.SYSTEM_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.GET_RUNNING_AGENTS);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);				
	}

	@Override
	public void startAgent(String type, String name) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "SYSTEM_AGENT", AgentTypeEnum.SYSTEM_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.START_AGENT);
		agentMsg.setContent(name);	
		agentMsg.setEncoding(type);
		
		messageManager.post(agentMsg);				
	}

	@Override
	public void stopAgent(AgentId agentId) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "SYSTEM_AGENT", AgentTypeEnum.SYSTEM_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.STOP_AGENT);
		agentMsg.setContentObj(agentId);
		
		messageManager.post(agentMsg);				
	}

	@Override
	public void sendACLMessage(ACLMessage message) {
		messageManager.post(message);				
	}

	@Override
	public void getPerformatives(String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, "SYSTEM_AGENT", AgentTypeEnum.SYSTEM_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.GET_PERFORMATIVES);
		agentMsg.setContent(username);	
		
		messageManager.post(agentMsg);				
	}

}
