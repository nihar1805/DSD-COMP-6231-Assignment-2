import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public interface IRegistry {
    void register(Repository1_Impl r1obj) throws RemoteException, AlreadyBoundException;
    void register(Repository2_Impl r2obj) throws RemoteException, AlreadyBoundException;
    void register(Repository3_Impl r3obj) throws RemoteException, AlreadyBoundException;
}
