package agentmanager;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import agentcenter.AgentCenter;
import agents.Agent;
import agents.AgentId;
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

	@EJB
	private AgentCenter agentCenter;

	/**
	 * Default constructor.
	 */
	public CachedAgentsBean() {
		runningAgents = new HashSet<Agent>();
	}
	

	@Override
	public Agent getById(AgentId agentId) {
		return runningAgents.stream().filter(a -> a.getAgentId().equals(agentId)).findFirst().orElse(null);
	}

	@Override
	public Set<Agent> getRunningAgents() {
		return runningAgents;
	}

	@Override
	public void addRunningAgent(Agent agent) {
		runningAgents.add(agent);
		ws.sendMessageToAllActive(
				"RUNNING_AGENT&" + agent.getAgentId().getName() + "&" + agent.getAgentId().getType().toString() + "&"
						+ agent.getAgentId().getHost().getAlias() + "&" + agent.getAgentId().getHost().getAddress());

		agentCenter.notifyAllNewAgent(agent);
	}

	@Override
	public void stopAgent(AgentId agentId) {
		Agent toRemove = getById(agentId);
		if (toRemove != null) {
			agentCenter.notifyAllAgentQuit(toRemove);
			
			runningAgents.remove(toRemove);
			ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + agentId.getName() + "&" + agentId.getType().toString()
					+ "&" + agentId.getHost().getAlias() + "&" + agentId.getHost().getAddress());
		}
	}

	@Override
	public void stopAgentFromRemote(Agent agent) {
		Agent toRemove = getById(agent.getAgentId());
		if (toRemove != null) {			
			runningAgents.remove(toRemove);
			ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + toRemove.getAgentId().getName() + "&" + toRemove.getAgentId().getType().toString()
					+ "&" + toRemove.getAgentId().getHost().getAlias() + "&" + toRemove.getAgentId().getHost().getAddress());
		}
	}

	@Override
	public void addRunningAgentFromRemote(Agent agent) {
		runningAgents.add(agent);
		ws.sendMessageToAllActive(
				"RUNNING_AGENT&" + agent.getAgentId().getName() + "&" + agent.getAgentId().getType().toString() + "&"
						+ agent.getAgentId().getHost().getAlias() + "&" + agent.getAgentId().getHost().getAddress());
	}


	@Override
	public void removeFromNode(String alias) {
		for (Agent agent: runningAgents) {			
			if (agent.getAgentId().getHost().getAlias().equals(alias)) {
				ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + agent.getAgentId().getName() + "&" + agent.getAgentId().getType().toString()
						+ "&" + agent.getAgentId().getHost().getAlias() + "&" + agent.getAgentId().getHost().getAddress());
			}			
		}
		
		runningAgents.removeIf(a -> a.getAgentId().getHost().getAlias().equals(alias));		
	}

}
