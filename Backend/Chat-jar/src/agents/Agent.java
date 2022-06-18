package agents;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remote;
import messagemanager.ACLMessage;
import models.SearchResult;

@Remote
public interface Agent extends Serializable {
	
	public AgentId init(AgentId agentId);
	public void handleMessage(ACLMessage message);
	public AgentId getAgentId();
	public List<SearchResult> getSearchResults();
	
}
