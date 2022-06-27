import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public interface IDistributedRepository {
    int aggregate(String message) throws RemoteException, NotBoundException, RepException;
}
