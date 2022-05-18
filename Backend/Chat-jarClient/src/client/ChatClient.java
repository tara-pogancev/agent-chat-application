package client;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import util.JNDILookup;

public class ChatClient {

	private AgentManagerRemote agentManager = JNDILookup.lookUp(JNDILookup.AgentManagerLookup, AgentManagerBean.class);

	public void postConstruct() {
		agentManager.startAgent(JNDILookup.ChatAgentLookup);
	}

	public ChatClient() {
		postConstruct();
	}

	public static void main(String[] args) {
		new ChatClient();
	}

}
