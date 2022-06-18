package agents;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import agentmanager.CachedAgentsRemote;
import chatmanager.ChatManagerRemote;
import messagemanager.ACLMessage;
import messagemanager.MessageManagerRemote;
import models.ChatMessage;
import models.SearchResult;
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
		ChatMessage chatMessage = new ChatMessage();

		switch (message.getPerformative()) {
		case SEND_MESSAGE:
			chatMessage = new ChatMessage();
			chatMessage.setSender((String) message.getUserArgs().get("sender"));
			chatMessage.setSubject((String) message.getUserArgs().get("subject"));
			chatMessage.setContent((String) message.getUserArgs().get("content"));
			chatMessage.setReciever((String) message.getUserArgs().get("target"));

			chatManager.saveNewMessage(chatMessage);

			ws.sendMessage(chatMessage.reciever, chatMessage);
			break;

		case SEND_GROUP_MESSAGE:
			chatMessage = new ChatMessage();
			chatMessage.setSender((String) message.getUserArgs().get("sender"));
			chatMessage.setSubject((String) message.getUserArgs().get("subject"));
			chatMessage.setContent((String) message.getUserArgs().get("content"));

			for (String groupReceiver : chatManager.getActiveUsernames()) {
				chatMessage.setReciever(groupReceiver);
				chatManager.saveNewMessage(chatMessage);
			}

			for (User groupReceiver : chatManager.getLoggedInRemote()) {
				chatMessage.setReciever(groupReceiver.getUsername());
				chatManager.saveNewMessage(chatMessage);
			}

			ws.sendMessageToAllActive(chatMessage);
			break;

		case GET_ACTIVE_USERS:
			username = message.getContent();
			List<String> activeUsers = chatManager.getActiveUsernames();
			List<User> activeUsersRemote = chatManager.getLoggedInRemote();
			for (String activeUser : activeUsers) {
				ws.sendMessage(username, "LOGIN&" + activeUser);
			}
			for (User activeUser : activeUsersRemote) {
				ws.sendMessage(username, "LOGIN&" + activeUser.getUsername());
			}
			break;

		case GET_REGISTERED_USERS:
			username = message.getContent();
			List<String> registeredUsers = chatManager.getRegisteredUsernames();
			for (String registeredUser : registeredUsers) {
				ws.sendMessage(username, "REGISTRATION&" + registeredUser);
			}
			break;

		case GET_MESSAGES:
			username = message.getContent();
			for (ChatMessage userChatMessage : chatManager.getMessagesByUser(username)) {
				ws.sendMessage(username, userChatMessage);
			}
			break;

		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for chat agent.");
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
	

	@Override
	public List<SearchResult> getSearchResults() {
		return new ArrayList<SearchResult>();
	}
}
