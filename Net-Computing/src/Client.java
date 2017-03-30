import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public  class Client extends java.rmi.server.UnicastRemoteObject implements ServerRemote{

    public Client() throws RemoteException{
    	try {
    		Registry registry = LocateRegistry.getRegistry(2002);
            ServerRemote server = (ServerRemote) registry.lookup("Date");
            System.out.println( server.getDate() );
        }
    	catch (java.io.IOException e) {
    		// I/O Error or bad URL
        }
    	catch (NotBoundException e) {
    		//NiftyServer isn't registered
    	}
    }

    public static void main(String[] args) throws Exception {
		Connection connection = null;
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		connection = connectionFactory.createConnection();
		Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("customerQueue");
	    Client client = new Client();
	    
        while(true){
        	Mem mem = null;
            CpuPerc cpuperc = null;
            Sigar sigar = new Sigar();
            try {
                mem = sigar.getMem();
                cpuperc = sigar.getCpuPerc();      
            } catch (SigarException se) {
                se.printStackTrace();
            }
            float memoryusage = (float) mem.getUsedPercent();
            float cpuusage = (float) cpuperc.getCombined()*100;
            
            ReportObject ro = new ReportObject(memoryusage,cpuusage);
        	ObjectMessage msg = session.createObjectMessage(ro);
        	MessageProducer producer = session.createProducer(queue);

        	producer.send(msg);
        	Thread.sleep(1*5*1000);
        }
    }
    
	@Override
	public Date getDate() throws RemoteException {
		return null;
	}

	@Override
	public ReportObject ReportObjet(float memoryusage, float cpuusage) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReportObject ReportObject(float memoryusage, float cpuusage) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
}