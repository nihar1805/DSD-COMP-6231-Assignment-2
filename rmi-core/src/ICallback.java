import java.rmi.Remote;

public interface ICallback extends Remote {

    String transform(String value) throws RepException;
}
