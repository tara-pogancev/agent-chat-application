package agents.webscraping;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import agents.AgentTypeEnum;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import models.SearchResult;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class WebScrapingMasterAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	
	@EJB
	public MessageManagerRemote messageManager;
	
	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);
	
	@EJB
	private WSChat ws;
	
	@EJB
	private CachedAgentsRemote cachedAgents;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		cachedAgents.addRunningAgent(this);
		return agentId;		
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch (message.getPerformative()) {
		case START_WEB_SEARCH:
			Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.WebScrapingSearchAgentLookup, agentId.getName()+"!"+message.getContent(), AgentTypeEnum.WEB_SCRAPING_SEARCH_AGENT);
			ACLMessage agentMsg = new ACLMessage();
			agentMsg.getRecievers().add(agent.getAgentId());	
			agentMsg.setSender(this.agentId);
			agentMsg.setPerformative(PerformativeEnum.REQUEST_FILTERED_DATA);
			agentMsg.setContent(message.content);				
			messageManager.post(agentMsg);	
			
			System.out.println("Starting web search master for " + agentId.name);
			break;

		case PASS_DATA_TO_USER:
			SearchResult result = (SearchResult) message.getContentObj();
			ws.sendMessage(agentId.name, "SEARCH_RESULT&" + result.location + "&" + result.price + "&" + result.title);
			break;

		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for master web scraping agent.");
			break;
		}

	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
