package agentmanager;

import javax.ejb.Remote;

import agents.Agent;
import agents.AgentId;
import agents.AgentTypeEnum;

@Remote
public interface AgentManagerRemote {
	
	public AgentId startAgent(String name, String agentName, AgentTypeEnum type);
	public Agent getAgentById(AgentId agentId);
	public Agent getAgentByIdOrStartNew(String name, String agentName, AgentTypeEnum type);
	public void stopAgent(AgentId agentId);
	public void stopLocalAgent(String agentName, AgentTypeEnum type);
	public AgentId startAgentRemote(String name, AgentId agentId);
	
}
