import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDistributedRepository extends Remote {
    int aggregate(String message) throws RepException, RemoteException;
}
