package agents;

import java.util.HashMap;

public interface CachedAgentsRemote {

	public HashMap<String, Agent> getRunningAgents();
	public void addRunningAgent(String key, Agent agent);
}
