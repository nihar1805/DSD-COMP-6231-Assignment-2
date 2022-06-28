import javax.imageio.spi.IIORegistry;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ClientApp {
    static IRepository obj = null;
    static Connector c = new Connector();
    static IDirectory_Impl id = new IDirectory_Impl();


    public static ArrayList<String> server_port_list = new ArrayList<String>();
    public static int portS1;
    public static int portS2;
    public static int portS3;
    private static Registry reg;

    public static void main(String[] args){

            try {
                id.peerDiscovery();
                Runnable PortReceiver = new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        id.receivePort();
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
                Scanner in = new Scanner(System.in);

                while (true) {

                    System.out.println("CLIENT: ");
                    message = in.nextLine();
                    if (message.equals("EXIT")) break;
//                System.out.println();
                    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);

                    String[] array = new String[0];
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
                    c.assignServer(message);

                    if (array[0].equals("SET")) {
                        String set_res = obj.set(key, Integer.parseInt(array[2]));
                        System.out.println("SERVER: " + set_res);
                    } else if (array[0].equals("ADD")) {
                        String add_res = obj.add(key, Integer.parseInt(array[2]));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("GET")) {
                        String add_res = String.valueOf(obj.get(key));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("DELETE")) {
                        String add_res = String.valueOf(obj.delete(key));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("LIST")) {
                        String add_res = String.valueOf(obj.list());
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("SUM")) {
                        String add_res = String.valueOf(obj.sum(key));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("RESET")) {
                        String add_res = String.valueOf(obj.delete_all());
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("MIN")) {
                        String add_res = String.valueOf(obj.min(key));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("MAX")) {
                        String add_res = String.valueOf(obj.max(key));
                        System.out.println("SERVER: " + add_res);
                    } else if (array[0].equals("DSUM")) {
                        String ports = message + " " + portS2 + " " + portS3;
                        String add_res = String.valueOf(obj.aggregate(ports));
                        System.out.println("SERVER: " + add_res);
                    }
                }
            }catch (Exception | RepException e) {
                e.printStackTrace();
            }
        }






}
