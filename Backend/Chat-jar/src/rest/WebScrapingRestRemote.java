package rest;

import java.util.List;

import javax.ejb.Remote;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import models.SearchResult;

@Remote
public interface WebScrapingRestRemote {
	
	@GET
	@Path("/search/{text}")
	@Consumes(MediaType.APPLICATION_JSON)
	public List<SearchResult> searchWeb(@PathParam("text") String text);

}
