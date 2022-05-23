package messagemanager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AgentMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4750922547689000321L;
	public Map<String, Serializable> userArgs;
	
	public AgentMessage() {
		userArgs = new HashMap<>();
	}
}
