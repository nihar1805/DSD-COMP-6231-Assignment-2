import java.net.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;

public class Repository_1 extends Repository1_Impl {
    public static Registry reg;
    private static DatagramSocket socket;


    protected Repository_1(int port) throws RemoteException, SocketException {
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws Exception, RepException {

        reg = LocateRegistry.createRegistry(7410);
        Repository1_Impl r1obj = new Repository1_Impl();
        IRegistry_Impl registry = new IRegistry_Impl();
        registry.register(r1obj);

//        Repository1_Impl r1obj = new Repository1_Impl();
//        Registry reg = LocateRegistry.createRegistry(7410);
//        reg.bind("r1", r1obj);
//        System.out.println("Repository Server 1 Running on port 9630");

        int port = 1234;
        Repository_1 server = new Repository_1(port);
        System.out.println("Server 1 is running...");
        Runnable PortRequestReceiver = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                peerDiscovery();
            }
        };
        Thread t = new Thread(PortRequestReceiver);
        t.start();

    }



    private static void peerDiscovery() {
        try {
            DatagramSocket udpSocket = new DatagramSocket(7777, InetAddress.getByName("0.0.0.0"));
            udpSocket.setBroadcast(true);
            byte[] buf = new byte[1000];
            while(true) {
                DatagramPacket request = new DatagramPacket(buf, buf.length, InetAddress.getByName("0.0.0.0"), 8888);
                udpSocket.receive(request);
                Thread.sleep(2000);
                byte[] port_msg = new byte[1000];
                port_msg = "S1 7410".getBytes();
                DatagramSocket ds = new DatagramSocket();
                DatagramPacket response = new DatagramPacket(port_msg,port_msg.length, request.getAddress(), 6231);
                ds.send(response);
                System.out.println("Port sent");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}