import java.rmi.Remote;

public interface ICallback extends Remote {
    String getValue() throws RepException;
}
