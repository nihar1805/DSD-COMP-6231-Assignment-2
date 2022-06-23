import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;

public class Repository_2 extends Repository2_Impl {
    private HashMap<String, List<Integer>> r2 = new HashMap<>();
    private static InetAddress clientAddress;
    private static Integer clientPort;

    protected Repository_2() throws RemoteException {
        super();
    }

    public static void main(String[] args) throws Exception {
        Repository_Impl r2obj = new Repository_Impl();
        Registry reg = LocateRegistry.createRegistry(5454);
        reg.bind("r2", r2obj);
        System.out.println("Repository Server 2 Running  on port 5454");

        Runnable UDPServer = new Runnable() {
            public void run() {
                try {
                    protocol(r2obj);
                } catch (IOException | RepException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(UDPServer);
        thread.start();

    }

    private static void protocol(Repository_Impl r2obj) throws IOException, RepException {
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
                ls = r2obj.set(msg_array[1], Integer.parseInt(msg_array[2]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);

            } else if (msg_array[0].equals( "DELETE")) {
                String ls = null;
                ls = r2obj.delete(msg_array[1]);
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if(msg_array[0].equals("LIST")) {
                List<String> list_keys = r2obj.list();
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
                List<Integer> res = r2obj.get(msg_array[1]);
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
                String ls = String.valueOf(r2obj.sum(msg_array[1]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("ADD")) {
                String ls = r2obj.add(msg_array[1], Integer.parseInt(msg_array[2]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("RESET")) {
                String ls = r2obj.delete_all();
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("MIN")) {
                String ls = String.valueOf(r2obj.min(msg_array[1]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if (msg_array[0].equals("MAX")) {
                String ls = String.valueOf(r2obj.max(msg_array[1]));
                buffer = ls.getBytes(StandardCharsets.UTF_8);
            }

            else if(msg_array[0].equals("DSUM")){

            }

            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            ds.send(response);
        }
    }

}