import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class TaskManager {
	
    private JFrame frame = new JFrame("Task Manager");
    private static JTextArea messageArea = new JTextArea(8, 60);
    
    public static void main(String[] args) throws URISyntaxException, Exception{
    	BrokerService broker = BrokerFactory.createBroker(new URI("broker:(tcp://localhost:61616)"));
		broker.start();
		Connection connection = null;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		connection = connectionFactory.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("customerQueue");
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(new ConsumerMessageListener(){
			public void onMessage(Message msg) {
			      try {
			    	  ObjectMessage receivedobj = (ObjectMessage) msg;
			    	  ReportObject ro = (ReportObject) receivedobj.getObject();
			    	  System.out.println("Task Manager received from Server CPU usage:"+ro.getCpuusage()+"% and Memory usage: "+ro.getMemoryusage()+"%");
			    	  if(ro.getCpuusage()>30 ||ro.getMemoryusage()>75){
			    		  messageArea.append("Client with ID:"+ro.getClientID()+" is overworking ! \n");
			    	  }
			      }
			      catch (JMSException e) {
			        System.err.println("Error reading message");
			      }
			    }
		});
		connection.start();
		TaskManager tm = new TaskManager();
		tm.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tm.frame.pack();
		tm.frame.setVisible(true);
	}
    
    public TaskManager(){
    	messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
        messageArea.append("Task Manager running with message queue ready to receive from server.\n");
    }
}