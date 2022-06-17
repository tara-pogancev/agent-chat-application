package rest;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.jaunt.*;
import com.jaunt.component.*;

import agents.Agent;
import agents.AgentTypeEnum;
import messagemanager.ACLMessage;
import messagemanager.PerformativeEnum;
import models.SearchResult;
import util.JNDILookup;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Stateless
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@LocalBean
@Path("/web")
public class WebScrapingRestRemoteBean implements WebScrapingRestRemote {

	@Override
	public List<SearchResult> searchWeb(String text) {
		List<SearchResult> retVal = new ArrayList<>();
		
		
		return retVal;
	}

}
