import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Repository1_Impl extends UnicastRemoteObject implements IRepository,IDistributedRepository{
    private HashMap<String, List<Integer>> repo = new HashMap<>();
    private Registry reg;

    protected Repository1_Impl() throws RemoteException {
        super();
    }

    @Override
    public String delete_all() throws RepException {
        repo.clear();
        return "OK";
    }

    @Override
    public String delete(String key) throws RepException {
        if (repo.containsKey(key)) {
            repo.remove(key);
        }
        return "OK";
    }

    @Override
    public String add(String key, int value) throws RepException {
        List<Integer> l1 = new ArrayList<>();
        l1 = repo.get(key);
        l1.add(value);
        repo.put(key, l1);
        return "OK";
    }

    @Override
    public List<String> list() throws RepException {
        List<String> l = new ArrayList<String>();
        if (!repo.isEmpty()) {
            for (String s : repo.keySet()){
                l.add(s);
            }
        } else {
            l.add("empty");
        }
        return l;
    }

    @Override
    public String set(String key, int value) throws RepException {
        List<Integer> value_list = new ArrayList();
        value_list.add(value);
        System.out.println(repo);
        if(!repo.containsKey(key)){
            repo.put(key, value_list);
        }
        else if (repo.containsKey(key)){
            repo.replace(key, value_list);
        }
        return "OK";
    }

    @Override
    public List<Integer> get(String key) throws RepException {
        if (!repo.containsKey(key)) {
            List<Integer> i = new ArrayList<>();
            i.add(0);
            return i;
        }

        return repo.get(key);
    }

    @Override
    public int sum(String key) throws RepException {
        List<Integer> values = repo.get(key);
        int sum = 0;
        for (int i = 0; i < values.size(); i++){
            sum = sum + values.get(i);
        }
//        System.out.println(values);
//        System.out.println(sum);
        return sum;
    }

    @Override
    public int min(String key) throws RepException {
        List<Integer> values = repo.get(key);
        int min = values.get(0);
        for(int i=0; i<values.size(); i++){
            if(values.get(i) < min){
                min = values.get(i);
            }
        }
        return min;
    }

    @Override
    public int max(String key) throws RepException {
        List<Integer> values = repo.get(key);
        int max = values.get(0);
        for(int i=0; i<values.size(); i++){
            if(values.get(i) > max){
                max = values.get(i);
            }
        }
        return max;
    }

    @Override
    public int aggregate(String message) throws RemoteException, NotBoundException, RepException {
        String[] msg_array = message.trim().split(" ");
        IRepository obj2 = null;
        int index1 = msg_array.length - 1;
        int index2 = msg_array.length - 2;
        int portS3 = Integer.parseInt(msg_array[index1]);
        int portS2 = Integer.parseInt(msg_array[index2]);
        String key = msg_array[1];

        List<String> dsum_list = new ArrayList<String>(Arrays.asList(msg_array));
        dsum_list.remove(index1);
        dsum_list.remove(index2);

        int flag = 0;
        int final_sum = 0;
        for(int i=3; i<dsum_list.size();i++){
            if(dsum_list.get(i).equals("r2")) {
                String msg = "DSUM " + key;
                reg = LocateRegistry.getRegistry(ClientApp.portS2);
                obj2 = (IRepository) reg.lookup("r2");
                final_sum = final_sum + obj2.sum(key);
            }
            else if(dsum_list.get(i).equals("r3")){
                reg = LocateRegistry.getRegistry(ClientApp.portS3);
                obj2 = (IRepository) reg.lookup("r3");
                final_sum = final_sum + obj2.sum(key);

            }
            else{
                flag = 1;
                System.out.println("ERR Non-existence or ambiguous repository r4");
            }
        }
        if (flag == 1) {
            return 0;
            }
        return final_sum;
    }
}
