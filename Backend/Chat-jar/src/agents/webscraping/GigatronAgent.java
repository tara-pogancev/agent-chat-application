package agents.webscraping;

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
import messagemanager.MessageManagerRemote;
import messagemanager.PerformativeEnum;
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
	
	@EJB
	public MessageManagerRemote messageManager;

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
		case REQUEST_ALL_DATA:
			ACLMessage respondingMsg = new ACLMessage();
			respondingMsg.setContentObj(this.searchResults);
			respondingMsg.getRecievers().add(message.sender);
			respondingMsg.setPerformative(PerformativeEnum.PASS_DATA_TO_USER);
			messageManager.post(respondingMsg);
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
