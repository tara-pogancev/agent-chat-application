package agents;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import chatmanager.ChatManagerRemote;
import messagemanager.MessageManagerRemote;
import models.User;
import util.JNDILookup;
import ws.WSChat;

@Stateful
@Remote(Agent.class)
public class SystemAgent implements Agent {

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
				try {					
					option = (String) tmsg.getObjectProperty("command");
					User user = new User();
					switch (option) {	
					case "LOGIN":
						user.setUsername((String) tmsg.getObjectProperty("username"));
						ws.notifyNewLogin(user.username);					
						break;
						
					case "REGISTER":
						user.setUsername((String) tmsg.getObjectProperty("username"));
						ws.notifyNewRegistration(user.username);	
						break;
						
					case "LOGOUT":
						user.setUsername((String) tmsg.getObjectProperty("username"));
						ws.closeSessionOnLogOut(user.username);
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
	public String init(String agentId) {
		agentId = "sys";
		cachedAgents.addRunningAgent(agentId, this);
		System.out.println("New SYSTEM AGENT initiated: " + agentId);
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
