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

	//private List<String> chatClients = new ArrayList<String>();

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
				String response = "";
				try {
					
					option = (String) tmsg.getObjectProperty("command");
					switch (option) {					
					case "LOG_IN":
						/*
						username = (String) tmsg.getObjectProperty("username");
						password = (String) tmsg.getObjectProperty("password");
						result = chatManager.login(username, password);

						response = "LOG_IN!Logged in: " + (result ? "Yes!" : "No!");
						break;
						*/
					case "GET_LOGGEDIN":
						response = "LOGGEDIN!";
						List<User> users = chatManager.loggedInUsers();
						for (User u : users) {
							response += u.toString() + "|";
						}
						break;
					case "NEW_MESSAGE":
						String content = (String) tmsg.getObjectProperty("content");	
						response = "New message: " + content;
						break;

					case "x":
						break;
					default:
						response = "ERROR! Option: " + option + " does not exist.";
						break;
					}
					System.out.println(response);
					ws.onMessage("tara", response);
					
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
