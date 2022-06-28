import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Connector {

    public Connector() {
        super();
    }

    private static Registry reg;

    public void assignServer(String message) throws RemoteException, NotBoundException {
        String[] array = message.trim().split(" ");
        try {
            if (message.contains("DSUM")) {
                reg = LocateRegistry.getRegistry(ClientApp.portS1);
                ClientApp.obj = (IRepository) reg.lookup("r1");
//                System.out.println("In assign server obj r1");
            } else {
                if (array[0].equals("RESET") || array[0].equals("LIST")) {
                    if (array.length == 1) {
                        reg = LocateRegistry.getRegistry(ClientApp.portS1);
                        ClientApp.obj = (IRepository) reg.lookup("r1");
                    } else if (array[1].equals("r2")) {
                        reg = LocateRegistry.getRegistry(ClientApp.portS2);
                        ClientApp.obj = (IRepository) reg.lookup("r2");
                    } else if (array[1].equals("r3")) {
                        reg = LocateRegistry.getRegistry(ClientApp.portS3);
                        ClientApp.obj = (IRepository) reg.lookup("r3");
                    } else if (!array[1].equals("r2") || !array[1].equals("r3") || !array[1].equals("r1")) {
                        System.out.println("Repository doesn't exist");
                    }

                } else {
                    if (message.contains(".")) {
                        String[] input_msg = message.trim().split(" ");
                        String[] rep = input_msg[1].split("[.]");

                        if (rep[0].equals("r2")) {
                            reg = LocateRegistry.getRegistry(ClientApp.portS2);
                            ClientApp.obj = (IRepository) reg.lookup("r2");
//                            System.out.println("Set r2.a ");
                        } else if (rep[0].equals("r3")) {
                            reg = LocateRegistry.getRegistry(ClientApp.portS3);
                            ClientApp.obj = (IRepository) reg.lookup("r3");
//                            System.out.println("set r3.a");
                        }
                    } else {
                        reg = LocateRegistry.getRegistry(ClientApp.portS1);
                        ClientApp.obj = (IRepository) reg.lookup("r1");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
