import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class Server extends java.rmi.server.UnicastRemoteObject implements ServerRemote{
	static String msgfortm = null;
	public Server() throws RemoteException { }
    // implement the ServerRemote interface
	public ReportObject ReportObject(float memoryusage, float cpuusage){
		return new ReportObject(memoryusage,cpuusage);
	}
	public Date getDate() throws RemoteException { 
		return new Date();
	}
    public static void main(String[] args) throws Exception {
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
			    	  System.out.println("Server received from Client CPU usage:"+ro.getCpuusage()+"% and Memory usage: "+ro.getMemoryusage()+"%");
			    	  if(ro.getCpuusage()>30 ||ro.getMemoryusage()>75){
			    		  msgfortm = "Client is overworking !";
			    		  System.err.println(msgfortm);
			    	  }
			      }
			      catch (JMSException e) {
			        System.err.println("Error reading message");
			      }
			    }
		});
		connection.start();
		
		System.out.println("The server is running.");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(9899);
        try {
        	LocateRegistry.createRegistry(2002);
            Registry registry = LocateRegistry.getRegistry(2002);       
        	ServerRemote server = new Server();
        	registry.rebind("Date", server);
        	registry.rebind("ReportObject", server);
        }
        catch (java.io.IOException e) {
        }
        try {
            while (true) {
                new TMconnection(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }
    
    private static class TMconnection extends Thread {
        private Socket socket;
        private int clientNumber;
        
        public TMconnection(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with task manager# " + clientNumber + " at " + socket);
        }

        public void run() {
            try {

                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                // Send a welcome message to the client.
                out.println("Hello, you are Task Manager #" + clientNumber + ".");

                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                	if(msgfortm != null){
                		System.err.println("Server sends to TM");
                		out.println(msgfortm);
                		msgfortm = null;
                	}
                	Thread.sleep(1000);
                }
            }
            catch (IOException | InterruptedException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    log("Couldn't close a socket, what's going on?");
                }
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        private void log(String message) {
            System.out.println(message);
        }
    }

	@Override
	public ReportObject ReportObjet(float memoryusage, float cpuusage) throws RemoteException {
		return null;
	}
}