import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IRepository extends Remote,IAggregate,IDistributedRepository {

    String delete_all() throws RepException, RemoteException;
    String delete(String key) throws RepException, RemoteException;
    String add(String key, int value) throws RepException, RemoteException;
    String list() throws RepException, RemoteException;
    String set(String key, int value) throws RepException, RemoteException;
    String get(String key) throws RepException, RemoteException;

    String enumKeys() throws RepException, RemoteException;

    String enumValues(String key) throws RepException, RemoteException;
}
