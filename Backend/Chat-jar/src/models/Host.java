package models;

import java.io.Serializable;

public class Host implements Serializable{

	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public String alias;
	public String address;
	
	public String getAlias() {
		return alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public Host() {}

	public Host(String alias, String address) {
		super();
		this.alias = alias;
		this.address = address;
	}

	@Override
	public boolean equals(Object obj) {
		Host host = (Host)obj;
		return host.getAddress().equals(this.address) && host.getAlias().equals(this.getAlias());
	}
	
	
	
}
