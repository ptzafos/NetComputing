import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface ServerRemote extends Remote {
	ReportObject ReportObjet(float memoryusage, float cpuusage,int clientID) throws RemoteException;
	Date getDate() throws RemoteException;
	//Object execute( WorkRequest work ) throws RemoteException;
}