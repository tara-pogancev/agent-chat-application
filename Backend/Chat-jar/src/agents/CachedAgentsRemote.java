package agents;

import java.util.Set;

public interface CachedAgentsRemote {

	public Set<Agent> getRunningAgents();
	public void addRunningAgent(Agent agent);
	public void stopAgent(AgentId agentId);
	public Agent getById(AgentId agentId);
}
