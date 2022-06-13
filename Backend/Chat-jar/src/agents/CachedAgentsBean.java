package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import ws.WSChat;

/**
 * Session Bean implementation class CachedAgents
 */
@Singleton
@LocalBean
@Remote(CachedAgentsRemote.class)
public class CachedAgentsBean implements CachedAgentsRemote {

	Set<Agent> runningAgents;
	
	@EJB
	private WSChat ws;

	/**
	 * Default constructor.
	 */
	public CachedAgentsBean() {
		runningAgents = new HashSet<Agent>();
	}

	@Override
	public Set<Agent>  getRunningAgents() {
		return runningAgents;
	}

	@Override
	public void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
		ws.sendMessageToAllActive("RUNNING_AGENT&" + agent.getAgentId().getName() + "&" + agent.getAgentId().getType().toString() + "&" + agent.getAgentId().getHost().getAlias());
	}

	@Override
	public void stopAgent(AgentId agentId) {
		Agent toRemove = getById(agentId);
		if (toRemove != null) {
			runningAgents.remove(toRemove);
		}
	}

	@Override
	public Agent getById(AgentId agentId) {
		return runningAgents.stream().filter(a -> a.getAgentId().equals(agentId)).findFirst().orElse(null);
	}
}
