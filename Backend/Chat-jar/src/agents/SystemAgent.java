package agents;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.Message;

import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class SystemAgent implements Agent  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;

	@EJB
	private ChatManagerRemote chatManager;
	
	@EJB
	private CachedAgentsRemote cachedAgents;
	
	@EJB
	private WSChat ws;

	@PostConstruct
	public void postConstruct() {
	}

	protected MessageManagerRemote msm() {
		return (MessageManagerRemote) JNDILookup.lookUp(JNDILookup.MessageManagerLookup, MessageManagerRemote.class);
	}

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		cachedAgents.addRunningAgent(this);
		return agentId;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
