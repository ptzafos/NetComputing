import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

public class ConsumerMessageListener implements MessageListener {
	private String consumerName;
	public ConsumerMessageListener(String consumerName) {
		this.consumerName = consumerName;
	}

	public void onMessage(Message message) {
		ObjectMessage receivedobj = (ObjectMessage) message;
		try {
			ReportObject ro = (ReportObject) receivedobj.getObject();
			System.out.println(consumerName + " received "+ ro.getCpuusage());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}