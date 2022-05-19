package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;


import chatmanager.ChatManagerRemote;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class ChatAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String agentId;
	private List<ChatMessage> clientMessages = new ArrayList<>();	

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
	public void handleMessage(Message message) {
		TextMessage tmsg = (TextMessage) message;		

		String receiver;
		try {
			receiver = (String) tmsg.getObjectProperty("receiver");
			if (agentId.equals(receiver)) {
				String option = "";
				String username = "";
				try {					
					option = (String) tmsg.getObjectProperty("command");
					switch (option) {	
					case "LOGIN":
						username = (String) tmsg.getObjectProperty("username");
						ws.notifyNewLogin(username);					
						break;
						
					case "REGISTER":
						username = (String) tmsg.getObjectProperty("username");
						ws.notifyNewRegistration(username);	
						break;
						
					case "LOGOUT":
						username = (String) tmsg.getObjectProperty("username");
						ws.closeSessionOnLogOut(username);
						break;
					
					case "NEW_GROUP_MESSAGE":
						ChatMessage chatMessage = new ChatMessage();
						chatMessage.setSender((String) tmsg.getObjectProperty("sender"));
						chatMessage.setSubject((String) tmsg.getObjectProperty("subject"));
						chatMessage.setContent((String) tmsg.getObjectProperty("content"));
						
						System.out.println("New group message: " + chatMessage.getContent());
						clientMessages.add(chatMessage);
						ws.sendMessageToAllActive(chatMessage);
						break;

					default:
						System.out.println( "ERROR! Option: " + option + " does not exist.");
						break;
					}
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String init(String id) {
		agentId = id;
		cachedAgents.addRunningAgent(agentId, this);
		System.out.println("New agent initiated: " + id);
		return agentId;
	}

	@Override
	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public List<ChatMessage> getClientMessages() {
		return clientMessages;
	}

	public void setClientMessages(List<ChatMessage> clientMessages) {
		this.clientMessages = clientMessages;
	}
		
}
