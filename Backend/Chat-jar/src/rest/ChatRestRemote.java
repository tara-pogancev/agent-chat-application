package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import models.ChatMessage;

@Remote
public interface ChatRestRemote {

	// AGENTS	
	@GET
	@Path("/users/loggedIn/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getLoggedInUsers(@PathParam("userId") String username);
	
	@GET
	@Path("/users/registered/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRegisteredUsers(@PathParam("userId") String username);

	@POST
	@Path("/messages/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessageToAllActive(ChatMessage message);
	
	@POST
	@Path("/messages/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendMessage(ChatMessage message);
	
	@GET
	@Path("/messages/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getUsersMessages(@PathParam("userId") String username);
	
	@DELETE
	@Path("/users/loggedIn/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void logOut(@PathParam("userId") String username);
}
