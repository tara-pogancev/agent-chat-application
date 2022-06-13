package messagemanager;

public enum PerformativeEnum {
	
	// https://github.com/milanvidakovic/siebog-lite
	ACCEPT_PROPOSAL,
	AGREE, 
	CANCEL, 
	CALL_FOR_PROPOSAL, 
	CONFIRM, 
	DISCONFIRM, 
	FAILURE, 
	INFORM, 
	INFORM_IF, 
	INFORM_REF, 
	NOT_UNDERSTOOD, 
	PROPAGATE, 
	PROPOSE, 
	PROXY, 
	QUERY_IF, 
	QUERY_REF,
	REFUSE, 
	REJECT_PROPOSAL, 
	REQUEST, 
	REQUEST_WHEN, 
	REQUEST_WHENEVER, 
	SUBSCRIBE,
	RESUME,
	
	// Chat Application
	SEND_MESSAGE,
	SEND_GROUP_MESSAGE,
	LOGIN,
	REGISTER,
	LOGOUT,
	GET_ACTIVE_USERS,
	GET_REGISTERED_USERS,
	GET_MESSAGES,
	
	// Client-Agent Communication
	GET_AGENT_TYPES, 
	GET_RUNNING_AGENTS,
	START_AGENT,
	STOP_AGENT,
	SEND_ACL_MESSAGE, 
	GET_PERFORMATIVES
	
}