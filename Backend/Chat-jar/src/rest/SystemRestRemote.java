package rest;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import agents.AgentId;
import messagemanager.ACLMessage;

@Remote
public interface SystemRestRemote {
	
	@GET
	@Path("/agents/classes/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getAgentTypes(@PathParam("userId") String username);
	
	@GET
	@Path("/agents/running/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getRunningAgents(@PathParam("userId") String username);

	@PUT
	@Path("/agents/running/{type}/{name}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void startAgent(@PathParam("type") String type,@PathParam("name") String name);
	
	@DELETE
	@Path("/agents/running")
	@Consumes(MediaType.APPLICATION_JSON)
	public void stopAgent(AgentId agentId);
	
	@POST
	@Path("/messages")
	@Consumes(MediaType.APPLICATION_JSON)
	public void sendACLMessage(ACLMessage message);
	
	@GET
	@Path("/messages/{userId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void getPerformatives(@PathParam("userId") String username);

}
