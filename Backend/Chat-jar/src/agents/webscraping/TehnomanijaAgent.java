package agents.webscraping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.swing.plaf.synth.SynthSpinnerUI;

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
public class TehnomanijaAgent implements Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AgentId agentId;
	private String searchUrl = "https://www.tehnomanija.rs/index.php?mod=catalog&op=thm_search&search_type=all_actions&submited=1&keywords=";
	private List<SearchResult> searchResults = new ArrayList<>();

	@EJB
	private CachedAgentsRemote cachedAgents;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		this.searchUrl += agentId.getName().toLowerCase().trim() + "&items_per_page=200";
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
//			      java.io.File myObj = new java.io.File("E:\\FTN\\tehnomanija.txt");
//			      if (myObj.createNewFile()) {
//			        System.out.println("File created: " + myObj.getName());
//			      } else {
//			        System.out.println("File already exists.");
//			      }			      
//
//			      FileWriter myWriter = new FileWriter("E:\\FTN\\tehnomanija.txt");
//			      myWriter.write(userAgent.doc.outerHTML());
//			      myWriter.close();
//			      
//			    } catch (IOException e) {
//			      System.out.println("An error occurred working with files.");
//			      e.printStackTrace();
//			    }		
			
			Elements productDivs = userAgent.doc.findEach("<div data-product-id");
			for (Element div : productDivs) {
				SearchResult result = new SearchResult();
				String html = div.outerHTML();
				System.out.println(html);
				result.setLocation("Tehnomanija");
				String title = html.split("data-name=\"")[1];
				result.setTitle(title.split("&")[0]);
				
				String price = html.split("data-price=\"")[1];
				price = price.split("\"")[0];
				Double priceDouble = Double.parseDouble(price);
				result.setPrice(priceDouble);
				
				System.out.println(result.toString());
				searchResults.add(result);
			}
			
			System.out.println("Tehnomanija agent finished web scraping " + searchResults.size() + " items.");
						
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
			System.out
					.println("ERROR! Option: " + message.getPerformative().toString() + " not defined for auth agent.");
			break;
		}
	}

	@Override
	public AgentId getAgentId() {
		return agentId;
	}

	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}

}
