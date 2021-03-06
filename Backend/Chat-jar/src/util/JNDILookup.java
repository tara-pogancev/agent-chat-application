package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import agents.Agent;
import agents.AuthAgent;
import agents.ChatAgent;
import agents.SystemAgent;
import agents.webscraping.DrTehnoAgent;
import agents.webscraping.GigatronAgent;
import agents.webscraping.TehnomanijaAgent;
import agents.webscraping.WebScrapingMasterAgent;
import agents.webscraping.WebScrapingSearchAgent;
import messagemanager.MessageManagerBean;
import messagemanager.MessageManagerRemote;

public abstract class JNDILookup {

	public static final String JNDIPathChat = "ejb:Chat-ear/Chat-jar//";
	public static final String AgentManagerLookup = JNDIPathChat + AgentManagerBean.class.getSimpleName() + "!"
			+ AgentManagerRemote.class.getName();
	public static final String MessageManagerLookup = JNDIPathChat + MessageManagerBean.class.getSimpleName() + "!"
			+ MessageManagerRemote.class.getName();
	public static final String ChatAgentLookup = JNDIPathChat + ChatAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String AuthAgentLookup = JNDIPathChat + AuthAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String SystemAgentLookup = JNDIPathChat + SystemAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String WebScrapingMasterAgentLookup = JNDIPathChat + WebScrapingMasterAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String WebScrapingSearchAgentLookup = JNDIPathChat + WebScrapingSearchAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String TehnomanijaAgentLookup = JNDIPathChat + TehnomanijaAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String GigatronAgentLookup = JNDIPathChat + GigatronAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";
	public static final String DrTehnoAgentLookup = JNDIPathChat + DrTehnoAgent.class.getSimpleName() + "!"
			+ Agent.class.getName() + "?stateful";

	@SuppressWarnings("unchecked")
	public static <T> T lookUp(String name, Class<T> c) {
		T bean = null;
		try {
			Context context = new InitialContext();

			// System.out.println("Looking up: " + name);
			bean = (T) context.lookup(name);

			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}
}
