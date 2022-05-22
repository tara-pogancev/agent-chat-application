package agents;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Singleton;

/**
 * Session Bean implementation class CachedAgents
 */
@Singleton
@LocalBean
@Remote(CachedAgentsRemote.class)
public class CachedAgentsBean implements CachedAgentsRemote{

	HashMap<String, Agent> runningAgents;

	/**
	 * Default constructor.
	 */
	public CachedAgentsBean() {
		runningAgents = new HashMap<>();
	}

	@Override
	public HashMap<String, Agent> getRunningAgents() {
		return runningAgents;
	}

	@Override
	public void addRunningAgent(String key, Agent agent) {
		runningAgents.put(key, agent);
	}

	@Override
	public void stopAgent(String username) {
		runningAgents.remove(username);
	}

	@Override
	public HashMap<String, Agent> getAll() {
		return runningAgents;
	}	

}
