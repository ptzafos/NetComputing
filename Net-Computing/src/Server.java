import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Server extends java.rmi.server.UnicastRemoteObject implements ServerRemote{
	static java.sql.Connection sqlcon = null;
	public Server() throws RemoteException { }
    // implement the ServerRemote interface
	public ReportObject ReportObject(float memoryusage, float cpuusage,int clientID){
		return new ReportObject(memoryusage,cpuusage,clientID);
	}
	public Date getDate() throws RemoteException { 
		return new Date();
	}
    public static void main(String[] args) throws Exception {
    	
		System.out.println("The server is running.");
		System.out.println("Communication via message queue with task manager and via Sockets with Clients");
		
		System.out.println("Connecting database...");
		sqlcon = connectionQuerry();
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
            log("New connection with client #" + clientNumber + " at " + socket);
        }

        public void run() {
            try {
            	Connection connection = null;
        		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        		connection = connectionFactory.createConnection();
        		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
        		Queue queue = session.createQueue("customerQueue");
        		
                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed
                // after every newline.
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                // Send a welcome message to the client.
                out.println("Client " + clientNumber + " connected to server successfully.");

                // Get messages from the client, line by line; return them
                // capitalized
                while (true) {
                	ReportObject or = (ReportObject) in.readObject();
                	System.out.println("Received from client #"+or.getClientID() +" CPU usage:"+or.getCpuusage()+" memory usage:"+or.getMemoryusage());
                	if(or.getCpuusage()>30 || or.getMemoryusage()>50){
                		PreparedStatement st = sqlcon.prepareStatement("insert into performance values (?,?,?)");
                		st.setInt(1,or.getClientID());
                		st.setFloat(2,or.getCpuusage());
                		st.setFloat(3,or.getMemoryusage());
                		st.executeUpdate();
                		
                		System.err.println("Server sends to TM");
                		ObjectMessage msg = session.createObjectMessage(or);
                    	MessageProducer producer = session.createProducer(queue);
                    	producer.send(msg);
                	}
                	Thread.sleep(1000);
                }
            }
            catch (IOException | InterruptedException | ClassNotFoundException | JMSException | SQLException e) {
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
    public static java.sql.Connection connectionQuerry() throws ClassNotFoundException
	{
    	String url = "jdbc:mysql://127.0.0.1:3306/client_performance?useSSL=false";
    	java.sql.Connection sqlcon = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			sqlcon = DriverManager.getConnection(url, "root", "lk1995sf");
			System.out.println("Remote DB connection established");
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println("Remote server could not be connected");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Remote db connection establishment error");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("False querry");
		}
		return sqlcon;
	}
	@Override
	public ReportObject ReportObjet(float memoryusage, float cpuusage,int clientID) throws RemoteException {
		return null;
	}
}