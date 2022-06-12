package agents;

import java.io.Serializable;

import javax.ejb.Remote;
import messagemanager.ACLMessage;

@Remote
public interface Agent extends Serializable {
	
	public AgentId init(AgentId agentId);
	public void handleMessage(ACLMessage message);
	public AgentId getAgentId();
	
}
