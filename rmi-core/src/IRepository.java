import java.rmi.Remote;
import java.util.List;

public interface IRepository extends Remote,IAggregate {

    public String delete_all() throws RepException;
    public String delete(String key) throws RepException;
    public String add(String key, int value) throws RepException;
    public List<String> list() throws RepException;
    public String set(String key, int value) throws RepException;
    public List<Integer> get(String key) throws RepException;
}
