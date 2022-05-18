package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

public class ChatMessage implements Serializable {

	/**
	 * For code simplicity, DTO === model 
	 */
	public static final long serialVersionUID = 1L;
	public List<User> recievers = new ArrayList<>();
	public String sender;
	public Date date = new Date();
	public String subject;
	public String content;
	
	public List<User> getReciever() {
		return recievers;
	}
	
	public void setReciever(List<User> reciever) {
		this.recievers = reciever;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public ChatMessage() {}

	public ChatMessage(List<User> recievers, String sender, Date date, String subject, String content) {
		super();
		this.recievers = recievers;
		this.sender = sender;
		this.date = date;
		this.subject = subject;
		this.content = content;
	}	
	
	public String toJson() {
		Gson gson = new Gson();
	    String json = gson.toJson(this);
		return json;
	}
}
