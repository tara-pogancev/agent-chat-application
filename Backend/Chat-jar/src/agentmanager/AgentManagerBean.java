package agentmanager;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import agents.Agent;
import agents.CachedAgentsRemote;
import util.JNDILookup;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Stateless
@LocalBean
public class AgentManagerBean implements AgentManagerRemote {
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	
    public AgentManagerBean() {
        
    }

	@Override
	public String startAgent(String name, String agentId) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		return agent.init(agentId);
	}

	@Override
	public Agent getAgentById(String agentId) {
		return cachedAgents.getRunningAgents().get(agentId);
	}

	@Override
	public Agent getAgentByIdOrStartNew(String name, String id) {
		if (getAgentById(id) == null) {
			Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
			agent.init(id);
			return agent;
		} else {
			return getAgentById(id);
		}
	}


}
