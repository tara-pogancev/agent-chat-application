package connectionmanager;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public interface ConnectionRest {
	
	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerNewNode(String alias);
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response notifyNewNode(String alias);
	
	@POST
	@Path("/nodes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getNodes();
	
	@POST
	@Path("/users/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllLoggedIn();
	
	@DELETE
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteNode(String alias);
	
	@GET
	@Path("/ping")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ping(String alias);
}
