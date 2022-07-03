import java.rmi.RemoteException;

public class RepException extends Throwable{
    private String message;

    public RepException(String s) {
        super(s);
        this.message = s;
        System.out.println(s);
    }
}
