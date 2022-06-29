import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Repository_3 extends Repository3_Impl {
    private static DatagramSocket socket;
    private static InetAddress clientAddress;
    private static int clientPort;

    protected Repository_3(int port) throws RemoteException, SocketException {
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws Exception, RepException {
        Repository3_Impl r3obj = new Repository3_Impl();
        IRegistry_Impl registry = new IRegistry_Impl();
        registry.register(r3obj);

//        Registry reg = LocateRegistry.createRegistry(6495);
//        reg.bind("r3", r3obj);
//        System.out.println("Repository Server 3 Running on port 6495");

        int port = 8456;
        Repository_3 server = new Repository_3(port);
        System.out.println("Server 3 is running...");
        Runnable PortRequestReceiver = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                peerDiscovery();
                receiveRequestFromAnotherServer(r3obj);
            }
        };
        Thread t = new Thread(PortRequestReceiver);
        t.start();


    }


    private static void peerDiscovery() {
        try {
            DatagramSocket udpSocket = new DatagramSocket(9999, InetAddress.getByName("0.0.0.0"));
            udpSocket.setBroadcast(true);
            byte[] buf = new byte[1000];

                DatagramPacket request = new DatagramPacket(buf, buf.length, InetAddress.getByName("0.0.0.0"), 8888);
                udpSocket.receive(request);
                Thread.sleep(2000);
                byte[] port_msg = new byte[1000];
                port_msg = "S3 6495".getBytes();
                DatagramSocket ds = new DatagramSocket();
                DatagramPacket response = new DatagramPacket(port_msg,port_msg.length, request.getAddress(), 6231);
                ds.send(response);
                System.out.println("Port sent");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void receiveRequestFromAnotherServer(Repository3_Impl r2obj) {
        try {
            DatagramSocket ds = new DatagramSocket(6495);
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            ds.receive(request);
            clientAddress = request.getAddress();
            clientPort = request.getPort();
            buffer = request.getData();
            String message = new String(buffer, StandardCharsets.UTF_8);
            String[] msg_array = message.trim().split(" ");
            String key = msg_array[1];

            int sum = r2obj.sum(key);
            byte[] send_result = String.valueOf(sum).getBytes();
            DatagramPacket send_reply = new DatagramPacket(send_result,send_result.length, clientAddress, clientPort);
            ds.send(send_reply);

        } catch (RepException | IOException e) {
            e.printStackTrace();
        }
    }

}