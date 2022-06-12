package messagemanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agents.AgentId;

public class ACLMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4750922547689000321L;
	public Map<String, Serializable> userArgs = new HashMap<>();
	private PerformativeEnum performative;
	private AgentId sender;
	private List<AgentId> recievers = new ArrayList<>();
	private AgentId replyTo;
	private String content;
	private Object contentObj;
	private String language;
	private String encoding;
	private String ontology;
	private String conversationId;
	private String replyWith;
	private String inReplyTo;
	private Long replyBy;	
	
	public ACLMessage() {
		super();
	}

	public Map<String, Serializable> getUserArgs() {
		return userArgs;
	}

	public void setUserArgs(Map<String, Serializable> userArgs) {
		this.userArgs = userArgs;
	}

	public PerformativeEnum getPerformative() {
		return performative;
	}

	public void setPerformative(PerformativeEnum performative) {
		this.performative = performative;
	}

	public AgentId getSender() {
		return sender;
	}

	public void setSender(AgentId sender) {
		this.sender = sender;
	}

	public List<AgentId> getRecievers() {
		return recievers;
	}

	public void setRecievers(List<AgentId> recievers) {
		this.recievers = recievers;
	}

	public AgentId getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(AgentId replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Object getContentObj() {
		return contentObj;
	}

	public void setContentObj(Object contentObj) {
		this.contentObj = contentObj;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getReplyWith() {
		return replyWith;
	}

	public void setReplyWith(String replyWith) {
		this.replyWith = replyWith;
	}

	public String getInReplyTo() {
		return inReplyTo;
	}

	public void setInReplyTo(String inReplyTo) {
		this.inReplyTo = inReplyTo;
	}

	public Long getReplyBy() {
		return replyBy;
	}

	public void setReplyBy(Long replyBy) {
		this.replyBy = replyBy;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
