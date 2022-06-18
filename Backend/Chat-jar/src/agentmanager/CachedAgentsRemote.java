package agentmanager;

import java.util.Set;

import agents.Agent;
import agents.AgentId;

public interface CachedAgentsRemote {

	public Set<Agent> getRunningAgents();
	public void addRunningAgent(Agent agent);
	public void stopAgent(AgentId agentId);
	public Agent getById(AgentId agentId);
	public void addRunningAgentFromRemote(AgentId agentId);
	public void stopAgentFromRemote(AgentId agentId);
	public void removeFromNode(String alias);
	public boolean isAgentLocal(AgentId agentId);
}
