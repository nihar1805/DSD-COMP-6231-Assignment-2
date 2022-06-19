import javax.imageio.spi.IIORegistry;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ClientApp {
    static Registry reg = null;
    static IRepository obj = null;

    private static ArrayList<String> server_port_list = new ArrayList<String>();
    private static int portS1;
    private static int portS2;
    private static int portS3;

        public static void main(String[] args){
            try {
                peerDiscovery();
                Runnable PortReceiver = new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        receiveport();
                    }
                };
                Thread t = new Thread(PortReceiver);
                t.start();
                Thread.sleep(5000);
                if(server_port_list.size()>=1){
                    t.stop();
                }
                for(int i=0;i<server_port_list.size();i++){
                    String[] temp = server_port_list.get(i).split(" ");
                    if(temp[0].equals("S1")) {
                        portS1 = Integer.parseInt(temp[1]);
//                    System.out.println("S1: " + portS1);
                    }
                    else if (temp[0].equals("S2")) {
                        portS2 = Integer.parseInt(temp[1]);
//                    System.out.println("S2: " + portS2);
                    }
                    else if (temp[0].equals("S3")) {
                        portS3 = Integer.parseInt(temp[1]);
//                    System.out.println("S3: " + portS3);
                    }
                }
                protocol();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private static void protocol() {
            String message = "";

            try {
                InetAddress address = InetAddress.getByName("localhost");
                DatagramSocket ds = new DatagramSocket();
                Scanner in = new Scanner(System.in);

                while (true) {

                    System.out.println("CLIENT: ");
                    message = in.nextLine();
                    if (message.equals("EXIT")) break;
//                System.out.println();
                    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

                    String[] array = message.trim().split(" ");

                    assignServer(message);

                    if(array[0].equals("SET")){
                        String set_res = obj.set(array[1], Integer.parseInt(array[2]));
                        System.out.println("SERVER: " + set_res);
                    }

                    else if (array[0].equals("ADD")) {
                        String add_res = obj.add(array[1], Integer.parseInt(array[2]));
                        System.out.println("SERVER: " + add_res);
                    }

                    else if(array[0].equals("GET")) {

                    }

                    else if (array[0].equals("DELETE")){

                    }

                    else if(array[0].equals("LIST")){

                    }

                    else if (array[0].equals("SUM")){

                    }

                    else if (array[0].equals("DSUM")) {

                    }

                    else if (array[0].equals("RESET")){

                    }

                    else if (array[0].equals("MIN")) {

                    }

                    else if (array[0].equals("MAX")) {

                    }
                }
            }catch (Exception | RepException e) {
                e.printStackTrace();
            }
        }

    public static void assignServer(String message) throws RemoteException, NotBoundException {
        String[] array = message.trim().split(" ");
        try {
            if (message.contains("DSUM")) {
                String ports = message + " " + portS2 + " " + portS3;

            } else if (array[0].equals("RESET") || array[0].equals("LIST")) {
                if (array.length == 1) {
                    reg = LocateRegistry.getRegistry(7410);
                    obj = (IRepository) reg.lookup("montreal");
                } else if (array[1].equals("r2")) {
                    reg = LocateRegistry.getRegistry(7410);
                    obj = (IRepository) reg.lookup("montreal");
                } else if (array[1].equals("r3")) {
                    reg = LocateRegistry.getRegistry(7410);
                    obj = (IRepository) reg.lookup("montreal");
                } else if (!array[1].equals("r2") || !array[1].equals("r3") || !array[1].equals("r1")) {
                    System.out.println("Repository doesn't exist");
                }

            } else {
                if (message.contains(".")) {
                    String[] input_msg = message.trim().split(" ");
                    String[] rep = input_msg[1].split("[.]");

                    if (rep[0].equals("r2")) {
                        reg = LocateRegistry.getRegistry(7410);
                        obj = (IRepository) reg.lookup("montreal");
                    } else if (rep[0].equals("r3")) {
                        reg = LocateRegistry.getRegistry(7410);
                        obj = (IRepository) reg.lookup("montreal");
                    }
                } else {
                    reg = LocateRegistry.getRegistry(7410);
                    obj = (IRepository) reg.lookup("montreal");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

        private static void peerDiscovery() {
            try {
                DatagramSocket udpmultiSocket = new DatagramSocket();
                udpmultiSocket.setBroadcast(true);
                byte[] msg = "Waiting for port...".getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(msg, msg.length, InetAddress.getByName("255.255.255.255"), 8888);
                udpmultiSocket.send(packet);

                Enumeration interfaces = null;

                interfaces = NetworkInterface.getNetworkInterfaces();
                while (interfaces.hasMoreElements()) {
                    NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                    if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                        continue;
                    }

                    for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                        InetAddress broadcast = interfaceAddress.getBroadcast();

                        if (broadcast == null) {
                            continue;
                        }

                        try {
                            DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, broadcast, 7777);
                            udpmultiSocket.send(sendPacket);

                            DatagramPacket sendPacket2 = new DatagramPacket(msg, msg.length, broadcast, 8888);
                            udpmultiSocket.send(sendPacket2);

                            DatagramPacket sendPacket3 = new DatagramPacket(msg, msg.length, broadcast, 9999);
                            udpmultiSocket.send(sendPacket3);
//                        System.out.println("Packet sent");

                        } catch (Exception e) {
                        }
                    }
                }
//            Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public static void receiveport() {
            try{
                DatagramSocket s = new DatagramSocket(6231, InetAddress.getByName("0.0.0.0"));
                while(true){
                    byte[] buf = new byte[1000];
                    DatagramPacket port_msg = new DatagramPacket(buf, buf.length);
//                System.out.println("waiting for server response");
                    s.receive(port_msg);
//                System.out.println("Response received from server");
                    String portString = new String(port_msg.getData()).trim();
//                System.out.println("Port Received : "+ portString);
                    server_port_list.add(portString);

                }

            }catch (Exception e){
                System.out.println(e);
            }
        }
}
