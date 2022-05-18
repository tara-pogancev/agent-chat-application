package messagemanager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * Session Bean implementation class MessageManagerBean
 */
@Stateless
@LocalBean
public class MessageManagerBean implements MessageManagerRemote {

	/**
	 * Default constructor.
	 */
	public MessageManagerBean() {
	}

	@EJB
	private JMSFactory factory;

	private Session session;
	private MessageProducer defaultProducer;

	@PostConstruct
	public void postConstruct() {
		session = factory.getSession();
		defaultProducer = factory.getProducer(session);
	}

	@PreDestroy
	public void preDestroy() {
		try {
			session.close();
		} catch (JMSException e) {
		}
	}

	public void post(AgentMessage msg) {
		try {
			defaultProducer.send(createTextMessage(msg));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private Message createTextMessage(AgentMessage amsg) {
		Message msg = null ;
		try {
			msg = session.createTextMessage();
			for(String property : amsg.userArgs.keySet()) {
				msg.setObjectProperty(property, amsg.userArgs.get(property));
			}
			return msg;
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	@Override
	public Session getSession() {
		return factory.getSession();
	}

	@Override
	public MessageConsumer getConsumer() {
		return factory.getConsumer(session);
	}


}
