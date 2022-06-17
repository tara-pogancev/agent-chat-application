package agents.webscraping;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import messagemanager.ACLMessage;

@Stateful
@Remote(Agent.class)
public class WebScrapingMasterAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	
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
		case LOGIN:
			break;

		case REGISTER:
			break;

		case LOGOUT:
			break;

		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for auth agent.");
			break;
		}

	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
