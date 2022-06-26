import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAggregate extends Remote {
    int sum(String key) throws RepException, RemoteException;
    int min(String key) throws RepException, RemoteException;
    int max(String key) throws RepException, RemoteException;
}
