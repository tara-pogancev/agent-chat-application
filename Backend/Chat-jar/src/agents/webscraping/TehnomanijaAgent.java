package agents.webscraping;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateful;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
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

	@EJB
	public MessageManagerRemote messageManager;

	@Override
	public AgentId init(AgentId agentId) {
		this.agentId = agentId;
		this.searchUrl += agentId.getName().toLowerCase().trim().replaceAll(" ", "%20") + "&items_per_page=200";
		cachedAgents.addRunningAgent(this);
		System.out.println("Scraping: " + searchUrl);
		new Thread(() -> {
			try {
				webScrape();
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		return agentId;
	}

	private void webScrape() throws JsonIOException, IOException {
		try {
			UserAgent userAgent = new UserAgent();
			userAgent.visit(searchUrl);

			Elements productDivs = userAgent.doc.findEach("<div data-product-id");
			for (Element div : productDivs) {
				SearchResult result = new SearchResult();
				String html = div.outerHTML();
				result.setLocation("Tehnomanija");
				String title = html.split("data-name=\"")[1];
				result.setTitle(title.split("\"")[0]);

				String price = html.split("data-price=\"")[1];
				price = price.split("\"")[0];
				Double priceDouble = Double.parseDouble(price);
				result.setPrice(priceDouble);

				searchResults.add(result);
			}

			File file = new File(getPersonalFileName());
			file.createNewFile();
			FileWriter fileWriter = new FileWriter(getPersonalFileName());
			Gson gson = new Gson();
			String json = gson.toJson(searchResults);
			fileWriter.write(json);
			fileWriter.close();
			
			System.out.println("Tehnomanija agent finished web scraping " + searchResults.size() + " items.");

		} catch (JauntException e) {
			System.err.println(e);
		}
	}

	private String getPersonalFileName() {
		return "./" + this.agentId.name.replaceAll(" ", "") + "-tehnomanija" + ".json";
	}

	@Override
	public void handleMessage(ACLMessage message) {
		switch (message.getPerformative()) {
		case REQUEST_ALL_DATA:
			try {
				Gson gson = new Gson();
				Type resultListType = new TypeToken<ArrayList<SearchResult>>(){}.getType();
				searchResults = gson.fromJson(new FileReader(getPersonalFileName()), resultListType);
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Tehnomanija in: " + searchResults.size());
			for (SearchResult result : searchResults) {
				ACLMessage respondingMsg = new ACLMessage();
				respondingMsg.setContentObj(result);
				respondingMsg.getRecievers().add(message.sender);
				respondingMsg.setPerformative(PerformativeEnum.PASS_DATA_TO_USER);
				messageManager.post(respondingMsg);
			}
			break;

		default:
			System.out.println(
					"ERROR! Option: " + message.getPerformative().toString() + " not defined for Tehnomanija agent.");
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

	public List<SearchResult> getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(List<SearchResult> searchResults) {
		this.searchResults = searchResults;
	}

}
