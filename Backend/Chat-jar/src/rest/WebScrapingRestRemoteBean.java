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
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import util.JNDILookup;


@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/web")
public class WebScrapingRestRemoteBean implements WebScrapingRestRemote {
	
	@EJB
	public MessageManagerRemote messageManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);
	
	@Override
	public void searchWeb(String text, String username) {
		Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.WebScrapingMasterAgentLookup, username, AgentTypeEnum.WEB_SCRAPING_MASTER_AGENT);
		ACLMessage agentMsg = new ACLMessage();
		agentMsg.getRecievers().add(agent.getAgentId());		
		agentMsg.setPerformative(PerformativeEnum.START_WEB_SEARCH);
		agentMsg.setContent(text);			
		messageManager.post(agentMsg);	
	}

}
