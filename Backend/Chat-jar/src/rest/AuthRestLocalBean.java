package rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import chatmanager.ChatManagerRemote;
import models.User;

@Stateless
@Path("/chat")
public class AuthRestLocalBean implements AuthRestLocal {
	
	@EJB
	private ChatManagerRemote chatManager;
	
	@Override
	public Response register(User user) {
		boolean response = chatManager.register(new User(user.username, user.password, null));
		return Response.status(Status.OK).entity(response).build();
	}

	@Override
	public Response login(User user) {
		boolean response = chatManager.login(user.username, user.password);
		return Response.status(Status.OK).entity(response).build();
	}

}
