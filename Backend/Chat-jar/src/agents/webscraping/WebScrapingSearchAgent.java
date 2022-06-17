package agents.webscraping;

import java.util.List;

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

@Stateful
@Remote(Agent.class)
public class WebScrapingSearchAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;

	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	@EJB
	private CachedAgentsRemote cachedAgents;

	@EJB
	public MessageManagerRemote messageManager;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		cachedAgents.addRunningAgent(this);
		return agentId;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch (message.getPerformative()) {
		case REQUEST_FILTERED_DATA:
			ACLMessage agentMsg = new ACLMessage();
			for (Agent runningAgent : cachedAgents.getRunningAgents()) {
				if (runningAgent.getAgentId().getType().equals(AgentTypeEnum.TEHNOMANIJA_AGENT)
						|| runningAgent.getAgentId().getType().equals(AgentTypeEnum.GIGATRON_AGENT)
						|| runningAgent.getAgentId().getType().equals(AgentTypeEnum.DR_TEHNO_AGENT)) {
					agentMsg.getRecievers().add(runningAgent.getAgentId());
				}
			}
			agentMsg.setSender(this.agentId);
			agentMsg.setPerformative(PerformativeEnum.REQUEST_ALL_DATA);
			messageManager.post(agentMsg);

			System.out.println("Starting web search for " + agentId.name);
			break;

		case PASS_DATA_TO_USER:
			SearchResult result = (SearchResult) message.getContentObj();
			Agent agent = agentManager.getAgentByIdOrStartNew(JNDILookup.WebScrapingMasterAgentLookup,
					getMasterAgentName(), AgentTypeEnum.WEB_SCRAPING_MASTER_AGENT);
			if (result.getTitle().toLowerCase().contains(getSearchParam().toLowerCase())) {
				ACLMessage respondingMsg = new ACLMessage();
				respondingMsg.setContentObj(result);
				respondingMsg.getRecievers().add(agent.getAgentId());
				respondingMsg.setPerformative(PerformativeEnum.PASS_DATA_TO_USER);
				messageManager.post(respondingMsg);
			}
			break;

		default:
			System.out
					.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for auth agent.");
			break;
		}
	}

	private String getMasterAgentName() {
		return this.agentId.getName().split("&")[0];
	}

	private String getSearchParam() {
		return this.agentId.getName().split("&")[1];
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
