package rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import agentmanager.AgentManagerBean;
import agentmanager.AgentManagerRemote;
import messagemanager.MessageManagerRemote;
import models.SearchResult;
import util.JNDILookup;

@Remote
public interface WebScrapingRestRemote {
	
	@GET
	@Path("/search/{text}/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void searchWeb(@PathParam("text") String text, @PathParam("username") String username);

}
