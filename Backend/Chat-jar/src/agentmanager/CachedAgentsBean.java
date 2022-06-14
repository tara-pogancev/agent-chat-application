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
import util.JNDILookup;
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

	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

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
		
		if (agentCenter.getHost().equals(agent.getAgentId().getHost())) {
			agentCenter.notifyAllNewAgent(agent.getAgentId());
		}
	}

	@Override
	public void stopAgent(AgentId agentId) {
		Agent toRemove = getById(agentId);
		if (toRemove != null) {
			agentCenter.notifyAllAgentQuit(toRemove.getAgentId());

			runningAgents.remove(toRemove);
			ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + agentId.getName() + "&" + agentId.getType().toString()
					+ "&" + agentId.getHost().getAlias() + "&" + agentId.getHost().getAddress());
		}
	}

	@Override
	public void stopAgentFromRemote(AgentId agentId) {
		Agent toRemove = getById(agentId);
		if (toRemove != null) {
			runningAgents.remove(toRemove);
			ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + toRemove.getAgentId().getName() + "&"
					+ toRemove.getAgentId().getType().toString() + "&" + toRemove.getAgentId().getHost().getAlias()
					+ "&" + toRemove.getAgentId().getHost().getAddress());
		}
	}

	@Override
	public void addRunningAgentFromRemote(AgentId agentId) {
		System.out.println("Cached agents adding: " + agentId.getName());
		switch (agentId.getType()) {
		case AUTH_AGENT:
			agentManager.startAgentRemote(JNDILookup.AuthAgentLookup, agentId);
			break;

		case CHAT_AGENT:
			agentManager.startAgentRemote(JNDILookup.ChatAgentLookup, agentId);
			break;

		case SYSTEM_AGENT:
			agentManager.startAgentRemote(JNDILookup.SystemAgentLookup, agentId);
			break;

		}

		ws.sendMessageToAllActive(
				"RUNNING_AGENT&" + agentId.getName() + "&" + agentId.getType().toString() + "&"
						+ agentId.getHost().getAlias() + "&" + agentId.getHost().getAddress());

	}

	@Override
	public void removeFromNode(String alias) {
		for (Agent agent : runningAgents) {
			if (agent.getAgentId().getHost().getAlias().equals(alias)) {
				ws.sendMessageToAllActive("RUNNING_AGENT_QUIT&" + agent.getAgentId().getName() + "&"
						+ agent.getAgentId().getType().toString() + "&" + agent.getAgentId().getHost().getAlias() + "&"
						+ agent.getAgentId().getHost().getAddress());
			}
		}

		runningAgents.removeIf(a -> a.getAgentId().getHost().getAlias().equals(alias));
	}

}
