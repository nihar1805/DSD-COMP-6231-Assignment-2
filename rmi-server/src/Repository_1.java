import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Repository_1 extends Repository1_Impl {
    private HashMap<String, List<Integer>> r1 = new HashMap<>();
    private static InetAddress clientAddress;
    private static Integer clientPort;

    protected Repository_1() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws Exception {
        Repository1_Impl r1obj = new Repository_1();
        Registry reg = LocateRegistry.createRegistry(9630);
        reg.bind("r1", r1obj);
        System.out.println("Server Running for Sherbrooke on port 9630");

        Runnable UDPServer = new Runnable() {
            public void run() {
                try {
                    protocol(r1obj);
                } catch (IOException | RepException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(UDPServer);
        thread.start();

    }

    private static void protocol(Repository1_Impl r1obj) throws IOException, RepException {
        DatagramSocket  ds=null;
        ds = new DatagramSocket(1230);
        while (true) {
            byte[] buffer = new byte[1024];
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
//            System.out.println("Before receive");
            ds.receive(request);
//            System.out.println("After Receive");
            clientAddress = request.getAddress();
            clientPort = request.getPort();
            buffer = request.getData();
            String message = new String(buffer, StandardCharsets.UTF_8);
//            System.out.println(message);
            String[] msg_array = message.trim().split(" ");
//            System.out.println(msg_array);
            String result = "";
            if (msg_array[0].equals("SET")) {
//                System.out.println("set method call");
                String ls = null;
                ls = r1obj.set(msg_array[1], Integer.parseInt(msg_array[2]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);

            } else if (msg_array[0].equals( "DELETE")) {
                String ls = null;
                ls = r1obj.delete(msg_array[1]);
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if(msg_array[0].equals("LIST")) {
                List<String> list_keys = r1obj.list();
                String reply = "";
                if (list_keys.get(0).equals("empty")){
                    buffer = "No keys found".getBytes(StandardCharsets.UTF_8);
                } else {
                    for (String i : list_keys) {
                        reply += " " + i + ",";
                    }
                    reply = reply.substring(0, (reply.length() - 1));
                    buffer = ("OK " + reply).getBytes(StandardCharsets.UTF_8);
                }
//                System.out.println(reply);
            }

            else if(msg_array[0].equals("GET")) {
                List<Integer> res = r1obj.get(msg_array[1]);
                if (res.get(0) == 0){
                    buffer = "Key doesn't exist".getBytes(StandardCharsets.UTF_8);
                }
                else {
                    String reply = "";
                    for (Integer i : res) {
                        reply += " " + i.toString() + ",";
                    }
                    reply = reply.substring(0, (reply.length() - 1));
                    buffer = ("OK " + reply).getBytes(StandardCharsets.UTF_8);
//                    System.out.println(reply);
                }

            }

            else if (msg_array[0].equals("SUM")) {

            }

            else if (msg_array[0].equals("ADD")) {
                String ls = r1obj.add(msg_array[1], Integer.parseInt(msg_array[2]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("RESET")) {
                String ls = r1obj.delete_all();
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("MIN")) {
            }

            else if (msg_array[0].equals("MAX")) {
            }

            else if(msg_array[0].equals("DSUM")){

            }

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            ds.send(response);
        }
    }

}