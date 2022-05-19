package rest;

import javax.ejb.Remote;
import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.ChatMessage;
import models.User;

@Remote
public interface ChatRest {
	
	@POST
	@Path("/users/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(User user);
	
	@POST
	@Path("/users/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public void login(User user);
	
	// AGENTS	
	@GET
	@Path("/users/loggedIn")
	public void getloggedInUsers();
	
	@GET
	@Path("/users/registered")
	public void getregisteredUsers();

	@POST
	@Path("/messages/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessageToAllActive(ChatMessage message);
	
	@POST
	@Path("/messages/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(ChatMessage message);
	
	@GET
	@Path("/messages/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUsersMessages(@PathParam("id") String username);
	
	@DELETE
	@Path("/users/loggedIn/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logOut(@PathParam("id") String username);
}
