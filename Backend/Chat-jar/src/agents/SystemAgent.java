package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agentmanager.CachedAgentsRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
import models.SearchResult;
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

	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);
	
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
		String type = "";
		String name = "";
		AgentId agentId = new AgentId();

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

		case START_AGENT:
			type = message.getEncoding();
			name = message.getContent();
			AgentTypeEnum typeEnum = AgentTypeEnum.valueOf(type);
			try {
				switch (typeEnum) {
				case AUTH_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.AuthAgentLookup, name, typeEnum);
					break;
					
				case CHAT_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.ChatAgentLookup, name, typeEnum);
					break;
					
				case SYSTEM_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.SystemAgentLookup, name, typeEnum);
					break;		
					
				case WEB_SCRAPING_MASTER_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.WebScrapingMasterAgentLookup, name, typeEnum);
					break;	
					
				case WEB_SCRAPING_SEARCH_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.WebScrapingSearchAgentLookup, name, typeEnum);
					break;	
					
				case TEHNOMANIJA_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.TehnomanijaAgentLookup, name, typeEnum);
					break;	
					
				case GIGATRON_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.GigatronAgentLookup, name, typeEnum);
					break;	
					
				case DR_TEHNO_AGENT: 
					agentManager.getAgentByIdOrStartNew(JNDILookup.DrTehnoAgentLookup, name, typeEnum);
					break;	
				}
			} catch (Exception e) {
			}

			break;
			
		case STOP_AGENT:
			agentId = (AgentId) message.getContentObj();
			agentManager.stopAgent(agentId);
			break;

		case GET_PERFORMATIVES:
			username = message.getContent();
			for (PerformativeEnum performative : PerformativeEnum.values()) {
				ws.sendMessage(username, "PERFORMATIVE&" + performative.toString());
			}
			break;
			
		case PING_PONG:
			username = message.getInReplyTo();
			ws.sendMessage(username, "PONG&" + message.getContent());
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
	

	@Override
	public List<SearchResult> getSearchResults() {
		return new ArrayList<SearchResult>();
	}

}
