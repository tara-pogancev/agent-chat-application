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

import com.google.gson.Gson;

import chatmanager.ChatManagerRemote;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import models.User;
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
				ChatMessage chatMessage = new ChatMessage();
				
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
						
					case "NEW_MESSAGE":
						chatMessage = new ChatMessage();
						chatMessage.setSender((String) tmsg.getObjectProperty("sender"));
						chatMessage.setSubject((String) tmsg.getObjectProperty("subject"));
						chatMessage.setContent((String) tmsg.getObjectProperty("content"));
						String msgReceiver = (String) tmsg.getObjectProperty("msgReceiver");
						
						chatManager.saveNewMessage(chatMessage, msgReceiver);
						
						System.out.println("New message: " + chatMessage.getContent());
						ws.sendMessage(msgReceiver, chatMessage);
						break;

					
					case "NEW_GROUP_MESSAGE":
						chatMessage = new ChatMessage();
						chatMessage.setSender((String) tmsg.getObjectProperty("sender"));
						chatMessage.setSubject((String) tmsg.getObjectProperty("subject"));
						chatMessage.setContent((String) tmsg.getObjectProperty("content"));
						
						for (String groupReceiver: chatManager.getActiveUsers()) {
							chatManager.saveNewMessage(chatMessage, groupReceiver);
						}
						
						System.out.println("New group message: " + chatMessage.getContent());
						ws.sendMessageToAllActive(chatMessage);
						break;
						
					case "GET_ACTIVE_USERS":					
						List<String> activeUsers = chatManager.getActiveUsers();
						for (String activeUser: activeUsers) {
							ws.sendMessage(receiver, "LOGIN&"+activeUser);
						}
						break;
						
					case "GET_REGISTERED_USERS":
						List<String> registeredUsers = chatManager.getRegisteredUsers();
						for (String registeredUser: registeredUsers) {
							ws.sendMessage(receiver, "REGISTRATION&"+registeredUser);
						}
						break;
						
					case "GET_MESSAGES":
						chatMessage = new ChatMessage();
						chatMessage.setSender((String) tmsg.getObjectProperty("sender"));
						chatMessage.setSubject((String) tmsg.getObjectProperty("subject"));
						chatMessage.setContent((String) tmsg.getObjectProperty("content"));
						
						for (String groupReceiver: chatManager.getActiveUsers()) {
							chatManager.saveNewMessage(chatMessage, groupReceiver);
						}
						
						System.out.println("New group message: " + chatMessage.getContent());
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
		
}
