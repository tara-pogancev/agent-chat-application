package agentmanager;

import java.util.Set;

import agents.Agent;
import agents.AgentId;

public interface CachedAgentsRemote {

	public Set<Agent> getRunningAgents();
	public void addRunningAgent(Agent agent);
	public void stopAgent(AgentId agentId);
	public Agent getById(AgentId agentId);
	public void addRunningAgentFromRemote(Agent agent);
	public void stopAgentFromRemote(Agent agent);
	public void removeFromNode(String alias);
}
