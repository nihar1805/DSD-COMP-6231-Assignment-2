import java.net.ServerSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class IRegistry_Impl implements IRegistry{

    public IRegistry_Impl() throws RemoteException {
    }

    @Override
    public void register(Repository1_Impl r1obj) throws RemoteException, AlreadyBoundException {
        Registry reg = LocateRegistry.getRegistry(7474);
        reg.bind("r1", r1obj);
    }

    @Override
    public void register(Repository2_Impl r2obj) throws AlreadyBoundException, RemoteException {
        Registry reg = LocateRegistry.getRegistry(7474);
        reg.bind("r2", r2obj);

    }

    @Override
    public void register(Repository3_Impl r3obj) throws AlreadyBoundException, RemoteException {
        Registry reg = LocateRegistry.getRegistry(7474);
        reg.bind("r3", r3obj);
    }
}
