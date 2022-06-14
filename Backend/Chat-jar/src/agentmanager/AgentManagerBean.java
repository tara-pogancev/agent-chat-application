package agentmanager;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import agentcenter.AgentCenter;
import agents.Agent;
import agents.AgentId;
import agents.AgentTypeEnum;
import util.JNDILookup;

/**
 * Session Bean implementation class AgentManagerBean
 */
@Stateless
@LocalBean
public class AgentManagerBean implements AgentManagerRemote {
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private AgentCenter agentCenter;
		
    public AgentManagerBean() {
        
    }

	@Override
	public AgentId startAgent(String name, String agentName, AgentTypeEnum type) {
		Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
		AgentId agentId = new AgentId();
		agentId.setName(agentName);
		agentId.setType(type);
		agentId.setHost(agentCenter.getHost());
		return agent.init(agentId);
	}

	@Override
	public Agent getAgentById(AgentId agentId) {
		return cachedAgents.getById(agentId);
	}
	

	@Override
	public Agent getAgentByIdOrStartNew(String name, String agentName, AgentTypeEnum type) {
		AgentId agentId = new AgentId();
		agentId.setName(agentName);
		agentId.setType(type);
		agentId.setHost(agentCenter.getHost());
		
		if (getAgentById(agentId) == null) {
			Agent agent = (Agent) JNDILookup.lookUp(name, Agent.class);
			agent.init(agentId);
			System.out.println("New agent initiated: " + agentId.getName() + ". Currently running: " + cachedAgents.getRunningAgents().size() + "." );
			return agent;
		} else {
			return getAgentById(agentId);
		}
	}

	@Override
	public void stopAgent(AgentId agentId) {
		cachedAgents.stopAgent(agentId);
		System.out.println("Stopped agent: " + agentId.getName() + ". Currently running: " + cachedAgents.getRunningAgents().size() + "." );
	}

	@Override
	public void stopLocalAgent(String agentName, AgentTypeEnum type) {
		AgentId agentId = new AgentId();
		agentId.setName(agentName);
		agentId.setType(type);
		agentId.setHost(agentCenter.getHost());
		stopAgent(agentId);		
	}

}
