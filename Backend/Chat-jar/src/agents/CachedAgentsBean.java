package agents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

/**
 * Session Bean implementation class CachedAgents
 */
@Singleton
@LocalBean
@Remote(CachedAgentsRemote.class)
public class CachedAgentsBean implements CachedAgentsRemote {

	Set<Agent> runningAgents;

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
