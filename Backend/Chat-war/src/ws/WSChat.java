package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import models.ChatMessage;

@Singleton
@LocalBean
@ServerEndpoint("/ws/{username}")
public class WSChat {
	
	private Map<String, Session> sessions = new HashMap<String, Session>();

	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		sessions.put(username, session);
		System.out.println("Opened WebSocket: " + username);
	}

	@OnClose
	public void onClose(@PathParam("username") String username, Session session) {
		sessions.remove(username);
		System.out.println("Closed WebSocket: " + username);
	}

	@OnError
	public void onError(@PathParam("username") String username, Session session, Throwable t) {
		sessions.remove(username);
		System.out.println("ERROR: Websocket aborted by the host.");
	}
	
	public void closeSessionOnLogOut(String username) {
		sessions.remove(username);
		this.notifyLogOut(username);
	}

	public void sendMessage(String username, ChatMessage message) {
		Session session = sessions.get(username);
		if (session != null && session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message.toJson());
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} else {
			System.out.println("Message delivery failure: Looks like " +  username + " is offline.");
		}
	}
	

	public void sendMessageToAllActive(ChatMessage message) {
		for (Session session: sessions.values()) {
			if (session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText(message.toJson());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println("Message delivery failure: Looks like " + getUsernameFromSession(session.getId()) + " is offline.");
			}
		}			
	}

	public void notifyNewRegistration(String username) {
		for (Session session: sessions.values()) {
			if (session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("REGISTRATION&" + username);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void notifyNewLogin(String username) {
		for (Session session: sessions.values()) {
			if (session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("LOGIN&" + username);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void notifyLogOut(String username) {
		for (Session session: sessions.values()) {
			if (session != null && session.isOpen()) {
				try {
					session.getBasicRemote().sendText("LOGOUT&" + username);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}		
	}
	
	public void sendMessage(String receiver, String message) {
		Session session = sessions.get(receiver);
		if (session != null && session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		} else {
			System.out.println("Message delivery failure: Looks like " +  receiver + " is offline.");
		}
	}		
	
	private String getUsernameFromSession(String sessionId) {
	    for (Entry<String, Session> entry : sessions.entrySet()) {
	        if (Objects.equals(sessionId, entry.getValue().getId())) {
	            return entry.getKey();
	        }
	    }
		return "null";
	}

}
