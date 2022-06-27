import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IRepository extends Remote,IAggregate,IDistributedRepository {

    String delete_all() throws RepException, RemoteException;
    String delete(String key) throws RepException, RemoteException;
    String add(String key, int value) throws RepException, RemoteException;
    List<String> list() throws RepException, RemoteException;
    String set(String key, int value) throws RepException, RemoteException;
    List<Integer> get(String key) throws RepException, RemoteException;
}
