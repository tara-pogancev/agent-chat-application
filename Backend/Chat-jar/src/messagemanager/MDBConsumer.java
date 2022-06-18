package messagemanager;

import java.util.ArrayList;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import agentcenter.AgentCenter;
import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import ws.WSChat;

/**
 * Message-Driven Bean implementation class for: MDBConsumer
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/topic/publicTopic") })
public class MDBConsumer implements MessageListener {

	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private AgentCenter agentCenter;
	
	/**
	 * Default constructor.
	 */
	public MDBConsumer() {

	}

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		try {
			ACLMessage agentMessage = (ACLMessage) ((ObjectMessage) message).getObject();
			for (AgentId receiver : agentMessage.getRecievers()) {
				Agent agent = (Agent) cachedAgents.getById(receiver);
				if (agent != null && agent.getAgentId().getHost().equals(agentCenter.getHost())) {
					agent.handleMessage(agentMessage);
				} else if (agent != null) {
					ACLMessage agentMessageToForward = agentMessage;
					agentMessageToForward.setRecievers(new ArrayList<AgentId>());
					agentMessageToForward.getRecievers().add(receiver);
					agentCenter.forwardMessage(agentMessageToForward);
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}		
	}

}
