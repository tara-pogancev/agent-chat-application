package agents.webscraping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import com.jaunt.Element;
import com.jaunt.Elements;
import com.jaunt.JauntException;
import com.jaunt.UserAgent;

import agentmanager.CachedAgentsRemote;
import agents.Agent;
import agents.AgentId;
import messagemanager.ACLMessage;
import models.SearchResult;

@Stateful
@Remote(Agent.class)
public class GigatronAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	private String searchUrl = "https://gigatron.rs/pretraga?pojam=";
	private List<SearchResult> searchResults = new ArrayList<>();

	@EJB
	private CachedAgentsRemote cachedAgents;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		this.searchUrl += agentId.getName().toLowerCase().trim().replaceAll(" ", "%20") + "&page=true";
		cachedAgents.addRunningAgent(this);
		System.out.println("Scraping: " + searchUrl);
		new Thread(() -> {
			webScrape();
		}).start();
		return agentId;
	}

	private void webScrape() {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(searchUrl);
						
//			 try {
//			      java.io.File myObj = new java.io.File("E:\\FTN\\gigatron.txt");
//			      if (myObj.createNewFile()) {
//			        System.out.println("File created: " + myObj.getName());
//			      } else {
//			        System.out.println("File already exists.");
//			      }			      
//
//			      FileWriter myWriter = new FileWriter("E:\\FTN\\gigatron.txt");
//			      myWriter.write(userAgent.doc.outerHTML());
//			      myWriter.close();
//			      
//			    } catch (IOException e) {
//			      System.out.println("An error occurred working with files.");
//			      e.printStackTrace();
//			    }		
			
			Elements productDivs = userAgent.doc.findEach("<div data-id=");
			for (Element div : productDivs) {
				SearchResult result = new SearchResult();
				String html = div.outerHTML();
				System.out.println(html);
				result.setLocation("Gigatron");
				String title = div.findFirst("<h4>").innerHTML();
				result.setTitle(title);
				
				String price = div.findFirst("<meta itemProp=\"price\"").getAt("content");
				Double priceDouble = Double.parseDouble(price);
				result.setPrice(priceDouble);
				
				System.out.println(result.toString());
				searchResults.add(result);
			}
			
			System.out.println("Gigatron agent finished web scraping " + searchResults.size() + " items.");
						
		} catch (JauntException e) {
			System.err.println(e);
		}
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch (message.getPerformative()) {
		case LOGIN:
			break;

		case REGISTER:
			break;

		case LOGOUT:
			break;

		default:
			System.out.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for Gigatron agent.");
			break;
		}	
		
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

}
