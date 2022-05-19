package agentmanager;

import javax.ejb.Remote;

import agents.Agent;
import messagemanager.AgentMessage;
import rest.ChatRestRemoteBean;

@Remote
public interface AgentManagerRemote {
	
	public String startAgent(String name, String agentId);
	public Agent getAgentById(String agentId);
	public Agent getAgentByIdOrStartNew(String name, String agentId);
	default void getLoggedInUsers(ChatRestRemoteBean chatRestRemoteBean) {
		AgentMessage message = new AgentMessage();
		message.userArgs.put("receiver", "chat");
		message.userArgs.put("command", "GET_LOGGEDIN");
		
		chatRestRemoteBean.messageManager.post(message);
	}
	
}
