package messagequeue;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class JmsMessageAsynchronousQueueExample {
	public static void main(String[] args) throws URISyntaxException, Exception {
		BrokerService broker = BrokerFactory.createBroker(new URI(
				"broker:(tcp://localhost:61616)"));
		broker.start();
		Connection connection = null;
		try {
			// Producer
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
					"tcp://localhost:61616");
			connection = connectionFactory.createConnection();
			Session session = connection.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("customerQueue");
			String payload = "Important Task";
			Message msg = session.createTextMessage(payload);
			MessageProducer producer = session.createProducer(queue);
			System.out.println("Sending text '" + payload + "'");
			producer.send(msg);

			// Consumer
			MessageConsumer consumer = session.createConsumer(queue);
			consumer.setMessageListener(new ConsumerMessageListener("Consumer"));
			connection.start();
			Thread.sleep(1000);
			Thread.sleep(1000);
			Thread.sleep(1000);
			Thread.sleep(1000);
			//session.close();
			
			String payload2 = "Important Task 2";
			Message msg2 = session.createTextMessage(payload2);
			System.out.println("Sending text '" + payload2 + "'");
			producer.send(msg2);
			Thread.sleep(1000);
			Thread.sleep(1000);
			String payload3 = "Important Task 3";
			Message msg3 = session.createTextMessage(payload3);
			System.out.println("Sending text '" + payload3 + "'");
			producer.send(msg3);
		}
		finally{
			
		}
	}
}