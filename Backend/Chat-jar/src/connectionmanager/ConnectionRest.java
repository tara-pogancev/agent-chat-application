package connectionmanager;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Host;
import models.User;

public interface ConnectionRest {
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Host> registerNewNode(Host node);
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewNode(Host node);
	
	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<Host> getNodes();
	
	@POST
	@Path("/users/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public void syncLoggedIn(Host targetNode);
	
	@DELETE
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteNode(String alias);
	
	@GET
	@Path("/ping")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean ping();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void addLoggedInList(List<User> activeUsers);
	
}
