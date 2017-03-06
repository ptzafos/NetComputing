import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface ServerRemote extends Remote {
	Date getDate() throws RemoteException;
	//Object execute( WorkRequest work ) throws RemoteException;
}