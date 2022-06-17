package agents.webscraping;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import agents.AgentTypeEnum;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import models.SearchResult;

@Stateful
@Remote(Agent.class)
public class WebScrapingSearchAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	private AgentId respondingTo;

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
			this.respondingTo = message.getSender();
			ACLMessage agentMsg = new ACLMessage();
			for (Agent runningAgent : cachedAgents.getRunningAgents()) {
				if (runningAgent.getAgentId().getType().equals(AgentTypeEnum.TEHNOMANIJA_AGENT)
						|| runningAgent.getAgentId().getType().equals(AgentTypeEnum.GIGATRON_AGENT)
						|| runningAgent.getAgentId().getType().equals(AgentTypeEnum.DR_TEHNO_AGENT)) {
					System.out.println("Adding agent to list: " + runningAgent.getAgentId().name);
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
			System.out.println("Tehnomanija out: " + result.title);
			if (result.getTitle().toLowerCase().contains(this.getAgentId().name.toLowerCase())) {
				ACLMessage respondingMsg = new ACLMessage();
				respondingMsg.setContentObj(result);
				respondingMsg.getRecievers().add(respondingTo);
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

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
