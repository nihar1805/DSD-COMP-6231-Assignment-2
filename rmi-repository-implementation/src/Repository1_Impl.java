import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Repository1_Impl extends UnicastRemoteObject implements IRepository,IDistributedRepository{
    private HashMap<String, List<Integer>> repo = new HashMap<>();
    private Registry reg;

    protected Repository1_Impl() throws RemoteException {
    }

//    protected Repository1_Impl() throws RemoteException {
//        super();
//    }

    @Override
    public String delete_all() throws RepException {
        if (repo.isEmpty()) throw new RepException("Repository is Empty!!");
        else
            repo.clear();
        return "OK";
    }

    @Override
    public String delete(String key) throws RepException {
        if (repo.containsKey(key)) {
            repo.remove(key);
        } else {
            throw new RepException("Key does not exist");
        }
        return "OK";
    }

    @Override
    public String add(String key, int value) throws RepException {
        List<Integer> l1 = new ArrayList<>();
        if (repo.containsKey(key)){
            l1 = repo.get(key);
            l1.add(value);
            repo.put(key, l1);
        } else throw new RepException("Key does not exist");

        return "OK";
    }

    @Override
    public String list() throws RepException {
        List<String> l = new ArrayList<String>();
        String reply = "";
        if (!repo.isEmpty()) {
            for (String s : repo.keySet()){
                l.add(s);
            }
        } else {
            throw new RepException("Repository is Empty!!");
        }
        for (String i : l) {
            reply += " " + i + ",";
        }
        reply = reply.substring(0, (reply.length() - 1));
        return reply;
    }

    @Override
    public String set(String key, int value) throws RepException {
        List<Integer> value_list = new ArrayList();
        value_list.add(value);
//        System.out.println(repo);
        if(!repo.containsKey(key)){
            repo.put(key, value_list);
        }
        else if (repo.containsKey(key)){
            repo.replace(key, value_list);
        }
        return "OK";
    }

    @Override
    public String get(String key) throws RepException {
        String reply = "";
        if (!repo.containsKey(key)) {
            throw new RepException("Key does not exist!!");
        } else {
            List<Integer> res = repo.get(key);
            for (Integer i : res) {
                reply += " " + i.toString() + ",";
            }
        }
        reply = reply.substring(0, (reply.length() - 1));
        return reply;
    }

    @Override
    public int sum(String key) throws RepException {
        int sum;
        if (repo.containsKey(key)) {
            List<Integer> values = repo.get(key);
            sum = 0;
            for (int i = 0; i < values.size(); i++) {
                sum = sum + values.get(i);
            }
        } else throw new RepException("Key does not exist");

        return sum;
    }

    @Override
    public int min(String key) throws RepException {

        int min = 0;
        if (repo.containsKey(key)) {
            List<Integer> values = repo.get(key);
            min = values.get(0);
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) < min) {
                    min = values.get(i);
                }
            }
        } else throw new RepException("Key does not exist");
        return min;
    }

    @Override
    public int max(String key) throws RepException {

        int max = 0;
        if (repo.containsKey(key)) {
            List<Integer> values = repo.get(key);
            max = values.get(0);
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) > max) {
                    max = values.get(i);
                }
            }
        } else throw new RepException("Key does not exist");

        return max;
    }

    @Override
    public int aggregate(String message) throws RepException {
        String[] msg_array = message.trim().split(" ");
//        System.out.println("Inside aggregate");

        System.out.println(message);
        int index1 = msg_array.length - 1;
        int index2 = msg_array.length - 2;
        int portS3 = Integer.parseInt(msg_array[index1]);
        int portS2 = Integer.parseInt(msg_array[index2]);
        String key = msg_array[1];

        List<String> dsum_list = new ArrayList<String>(Arrays.asList(msg_array));
        dsum_list.remove(index1);
        dsum_list.remove(index2);

        int flag = 0;
        int final_sum = this.sum(key);

//        System.out.println(final_sum);

        for(int i=3; i<dsum_list.size();i++){
            if(dsum_list.get(i).equals("r2")) {
//                String msg = "DSUM " + key;
                final_sum += requestAnotherServer(key, portS2);
            }
            else if(dsum_list.get(i).equals("r3")){
                String msg = "DSUM " + key;
                final_sum += requestAnotherServer(key, portS3);
            }
            else{
                throw new RepException("Invalid Repository");
            }
        }
        return final_sum;
    }

    public int requestAnotherServer(String key, int port) {
        DatagramSocket ds = null;
        byte[] buffer = new byte[1024];

        int final_sum = 0;
        try {
            ds = new DatagramSocket();
            String msg = "DSUM " + key;
            buffer = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket request = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("localhost"), port);
            ds.send(request);
//            System.out.println("Packet Sent TO PORT" + port);
            byte[] buff = new byte[1024];

            DatagramPacket response2 = new DatagramPacket(buff, buff.length, InetAddress.getByName("localhost"), 2345);
            ds.receive(response2);
//            System.out.println("Packet Received");
            String set_res = new String(response2.getData(), StandardCharsets.UTF_8).replaceAll("\u0000.*", "");

            final_sum = Integer.parseInt(set_res);
//            System.out.println(final_sum);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
        return final_sum;
    }


    @Override
    public String enumKeys() throws RepException, RemoteException {
        String reply = "";
        ICallback_Impl callback = new ICallback_Impl();

        Set<String> l;
        List<String> enumKeys = new ArrayList<>();
        l = repo.keySet();

        if (l == null) throw new RepException("Key does not exist!!");

        else {
            for (String key : l) {
                enumKeys.add(String.valueOf(callback.transform(key)));
            }
        }
        for (String i : l) {
            reply += " " + i + ",";
        }

        if (reply!= " ,") {
            reply = reply.substring(0, (reply.length() - 1));
        }
        return reply;
    }

    @Override
    public String enumValues(String key) throws RepException, RemoteException {
        String reply = "";
        ICallback_Impl callback = new ICallback_Impl();

        List<Integer> values = repo.get(key);
        List<Integer> enumVal = new ArrayList<>();

        if (values == null) {
            throw new RepException("Key does not exist!!");
        } else {
            for (Integer value : values) {
                enumVal.add(Integer.valueOf(callback.transform(String.valueOf(value))));
            }
        }

        for (int i : enumVal) {
            reply += " " + i + ",";
        }
        reply = reply.substring(0, (reply.length() - 1));
        return reply;
    }
}
