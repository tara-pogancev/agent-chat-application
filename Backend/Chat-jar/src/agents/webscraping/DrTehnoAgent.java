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
public class DrTehnoAgent implements Agent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	private String searchUrl = "https://www.drtechno.rs/catalogsearch/result/?q=";
	private List<SearchResult> searchResults = new ArrayList<>();

	@EJB
	private CachedAgentsRemote cachedAgents;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		this.searchUrl += agentId.getName().toLowerCase().trim().replaceAll(" ", "%20");
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
//			      java.io.File myObj = new java.io.File("E:\\FTN\\drtehno.txt");
//			      if (myObj.createNewFile()) {
//			        System.out.println("File created: " + myObj.getName());
//			      } else {
//			        System.out.println("File already exists.");
//			      }			      
//
//			      FileWriter myWriter = new FileWriter("E:\\FTN\\drtehno.txt");
//			      myWriter.write(userAgent.doc.outerHTML());
//			      myWriter.close();
//			      
//			    } catch (IOException e) {
//			      System.out.println("An error occurred working with files.");
//			      e.printStackTrace();
//			    }		
			
			Elements productDivs = userAgent.doc.findEach("<div class=\"product details product-item-details\">");
			System.out.println(productDivs.size());
			for (Element div : productDivs) {
				SearchResult result = new SearchResult();
				String html = div.outerHTML();
				result.setLocation("DrTehno");
				String title = div.findFirst("<a>").innerHTML();
				result.setTitle(title.split("%")[0].trim());
				
				String price = div.findFirst("<span data-price-amount>").getAt("data-price-amount");
				Double priceDouble = Double.parseDouble(price);
				result.setPrice(priceDouble);
				
				System.out.println(result.toString());
				searchResults.add(result);
			}
			
			System.out.println("DrTehno agent finished web scraping " + searchResults.size() + " items.");
						
		} catch (JauntException e) {
			System.err.println(e);
		}
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch (message.getPerformative()) {
		case SEND_WEB_SCRAPING_DATA:
			
			break;

		default:
			System.out
					.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for DrTehno agent.");
			break;
		}
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}
		
}
