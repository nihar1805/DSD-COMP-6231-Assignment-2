import java.rmi.Remote;

public interface IAggregate extends Remote {
    public int sum(String key) throws RepException;
    public int min(String key) throws RepException;
    public int max(String key) throws RepException;
}
