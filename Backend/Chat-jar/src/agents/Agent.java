package agents;

import java.io.Serializable;

import javax.ejb.Remote;
import javax.jms.Message;

@Remote
public interface Agent extends Serializable {

	public String init(String agentId);
	public void handleMessage(Message message);
	public String getAgentId();
}
