package connectionmanager;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import models.ChatMessage;
import models.Host;
import models.User;

public interface ConnectionManager {
	
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> registerNewNode(String nodeAlias);
	
	@POST
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewNode(String nodeAlias);
	
	@POST
	@Path("/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> getNodes();
	
	@POST
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncLoggedIn(String targetNode);
	
	@DELETE
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteNode(String alias);
	
	@GET
	@Path("/ping")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean ping();

	@POST
	@Path("/user/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addLoggedInFromRemote(User user);
	
	@DELETE
	@Path("/user/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeLoggedInFromRemote(String user);
	
	@POST
	@Path("/message")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void addChatMessageFromRemote(ChatMessage msg);
	
	public void notifyAllNewMessage(ChatMessage msg);
	
	public void notifyAllNewLogin(String user);
	
	public void notifyAllLogout(String user);

	public Object getMasterAlias();

}
