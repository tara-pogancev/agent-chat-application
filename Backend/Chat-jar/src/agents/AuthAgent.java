package agents;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class AuthAgent implements Agent {

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
	public void handleMessage(ACLMessage message) {
		String username = "";

		switch (message.getPerformative()) {
		case LOGIN:
			username = message.getContent();
			// ws.notifyNewLogin(username);
			break;

		case REGISTER:
			username = message.getContent();
			ws.notifyNewRegistration(username);
			break;

		case LOGOUT:
			username = message.getContent();
			ws.closeSessionOnLogOut(username);
			break;

		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for auth agent.");
			break;
		}
	}

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		cachedAgents.addRunningAgent(this);
		return agentId;
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}
}
