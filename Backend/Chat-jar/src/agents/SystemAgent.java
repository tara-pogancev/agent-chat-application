package agents;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.Message;

import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import models.ChatMessage;
import models.User;
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
		String username = "";

		switch (message.getPerformative()) {
		case GET_AGENT_TYPES:
			username = message.getContent();
			for (AgentTypeEnum agentType : AgentTypeEnum.values()) {
				ws.sendMessage(username, "AGENT_TYPE&" + agentType.toString());
			}
			break;

		case GET_RUNNING_AGENTS:
			username = message.getContent();
			for (Agent agent : cachedAgents.getRunningAgents()) {
				ws.sendMessage(username, "RUNNING_AGENT&" + agent.getAgentId().getName() + "&" + agent.getAgentId().getType().toString() +
						"&" + agent.getAgentId().getHost().getAlias() + "&" + agent.getAgentId().getHost().getAddress() );
			}
			break;
//
//		case START_AGENT:
//
//			break;
//			
//		case STOP_AGENT:
//
//			break;
//			
//		case SEND_ACL_MESSAGE:
//
//			break;
//			
		case GET_PERFORMATIVES:
			username = message.getContent();
			for (PerformativeEnum performative : PerformativeEnum.values()) {
				ws.sendMessage(username, "PERFORMATIVE&" + performative.toString());
			}
			break;
			
		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for system agent.");
			break;
		}		
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
