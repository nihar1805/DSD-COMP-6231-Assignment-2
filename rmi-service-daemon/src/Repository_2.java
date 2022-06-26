import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;

public class Repository_2 extends Repository2_Impl {
    private static DatagramSocket socket;

    protected Repository_2(int port) throws RemoteException, SocketException {
        socket =  new DatagramSocket(port);
    }

    public static void main(String[] args) throws Exception, RepException {
        Repository1_Impl r2obj = new Repository1_Impl();
        Registry reg = LocateRegistry.createRegistry(5454);
        reg.bind("r2", r2obj);
        System.out.println("Repository Server 2 Running  on port 5454");

        int port = 7894;
        Repository_2 server = new Repository_2(port);
        System.out.println("Server 2 is running...");
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
            DatagramSocket udpSocket = new DatagramSocket(8888, InetAddress.getByName("0.0.0.0"));
            udpSocket.setBroadcast(true);
            byte[] buf = new byte[1000];
            while(true) {
                DatagramPacket request = new DatagramPacket(buf, buf.length, InetAddress.getByName("0.0.0.0"), 8888);
                udpSocket.receive(request);
                Thread.sleep(2000);
                byte[] port_msg = new byte[1000];
                port_msg = "S2 5454".getBytes();
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