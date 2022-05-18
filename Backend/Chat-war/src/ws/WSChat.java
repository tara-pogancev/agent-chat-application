package ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Singleton
@ServerEndpoint("/ws/{username}")
public class WSChat {
	private Map<String, Session> sessions = new HashMap<String, Session>();
	
	@OnOpen
	public void onOpen(@PathParam("username") String username, Session session) {
		sessions.put(username, session);
	}
	
	@OnClose
	public void onClose(@PathParam("username") String username, Session session) {
		sessions.remove(username);
	}
	
	@OnError
	public void onError(@PathParam("username") String username, Session session, Throwable t) {
		sessions.remove(username);
		t.printStackTrace();
	}
	
	public void onMessage(String username, String message) {
		Session session = sessions.get(username);
		sendMessage(session, message);
	}
	
	public void onMessage(String message) {
		sessions.values().forEach(session -> sendMessage(session, message));
	}
	
	public void sendMessage(Session session, String message) {
		if(session.isOpen()) {
			try {
				session.getBasicRemote().sendText(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
