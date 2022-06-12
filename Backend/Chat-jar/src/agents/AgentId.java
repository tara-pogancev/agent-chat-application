package agents;

import java.io.Serializable;

import models.Host;

public class AgentId implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Host host;
	private AgentTypeEnum type;
	
	public AgentId() {
		super();
	}	
	
	public AgentId(String name, Host host, AgentTypeEnum type) {
		super();
		this.name = name;
		this.host = host;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	public AgentTypeEnum getType() {
		return type;
	}
	public void setType(AgentTypeEnum type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		AgentId agentId = (AgentId)obj;
		return agentId.name.equals(this.name) && agentId.host.equals(this.host) && agentId.type.equals(this.type);
	}
		
	
}
