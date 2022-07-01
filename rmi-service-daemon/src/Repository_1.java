import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;

public class Repository_1 extends Repository1_Impl {

    static IRepository obj = null;
    public static Registry reg;
    private static DatagramSocket socket;
    private InetAddress clientAddress;
    private Integer clientPort;
    Connector c = new Connector();

    protected Repository_1(int port) throws RemoteException, SocketException {
        socket = new DatagramSocket(port);
    }

    public static void main(String[] args) throws Exception, RepException {

        reg = LocateRegistry.createRegistry(7474);
        Repository1_Impl r1obj = new Repository1_Impl();
        IRegistry_Impl registry = new IRegistry_Impl();
        registry.register(r1obj);

//        Repository1_Impl r1obj = new Repository1_Impl();
//        Registry reg = LocateRegistry.createRegistry(7410);
//        reg.bind("r1", r1obj);
//        System.out.println("Repository Server 1 Running on port 9630");

        int port = 1111;
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

        server.protocol();

    }

    private void protocol() throws IOException, NotBoundException {
        String add_res = "";
        DatagramSocket socket = new DatagramSocket(7410);

        while (true) {

            try{
                byte[] buffer = new byte[1024];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                clientAddress = request.getAddress();
                clientPort = request.getPort();
                buffer = request.getData();
                String message = new String(buffer, StandardCharsets.UTF_8);
                message = message.trim();


                c.assignServer(message);

                String[] array = message.trim().split(" ");
                String key = "";
                if (message.contains(".")) {
                    array = message.trim().split(" ");
                    String[] rep = array[1].split("[.]");
                    String repid = rep[0];
                    key = rep[1];
                } else {
                    array = message.trim().split(" ");
                    if(array.length > 1) {
                        key = array[1];
                    }
                }

                if (array[0].equals("SET")) {
                    add_res = obj.set(key, Integer.parseInt(array[2]));

                } else if (array[0].equals("ADD")) {
                    add_res = obj.add(key, Integer.parseInt(array[2]));

                } else if (array[0].equals("GET")) {
                    add_res = obj.get(key);

                } else if (array[0].equals("DELETE")) {
                    add_res = String.valueOf(obj.delete(key));

                } else if (array[0].equals("LIST")) {
                    add_res = String.valueOf(obj.list());

                } else if (array[0].equals("SUM")) {
                    add_res = String.valueOf(obj.sum(key));

                } else if (array[0].equals("RESET")) {
                    add_res = String.valueOf(obj.delete_all());

                } else if (array[0].equals("MIN")) {
                    add_res = String.valueOf(obj.min(key));

                } else if (array[0].equals("MAX")) {
                    add_res = String.valueOf(obj.max(key));

                } else if (array[0].equals("DSUM")) {
                    String ports = message + " " + 5454 + " " + 6495;
                    add_res = String.valueOf(obj.aggregate(ports));

                } else if (array[0].equals("ENUM")) {
                    if (array.length == 2) {
                        add_res = String.valueOf(obj.enumValues(key));
                    } else {
                        add_res = String.valueOf(obj.enumKeys());
                    }
                }



                byte[] buff = new byte[1024];

                buff = add_res.getBytes(StandardCharsets.UTF_8);
                DatagramPacket response = new DatagramPacket(buff, buff.length, clientAddress, clientPort);
                socket.send(response);


            }catch (Exception e) {
                e.printStackTrace();
            } catch (RepException e) {
                e.printStackTrace();
            }


        }
    }


    private static void peerDiscovery() {
        try {
            DatagramSocket udpSocket = new DatagramSocket(7777, InetAddress.getByName("0.0.0.0"));
            udpSocket.setBroadcast(true);
            byte[] buf = new byte[1000];

                DatagramPacket request = new DatagramPacket(buf, buf.length, InetAddress.getByName("0.0.0.0"), 8888);
                udpSocket.receive(request);
                Thread.sleep(2000);
                byte[] port_msg = new byte[1000];
                port_msg = "S1 7410".getBytes();

                DatagramSocket ds = new DatagramSocket();
                DatagramPacket response = new DatagramPacket(port_msg,port_msg.length, request.getAddress(), 6231);
                ds.send(response);
                System.out.println("Port sent");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}