import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Repository2_Impl extends UnicastRemoteObject implements IRepository{
    private static HashMap<String, List<Integer>> repo = new HashMap<>();

    protected Repository2_Impl() throws RemoteException {
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
    public String list() throws RepException {
        List<String> l = new ArrayList<String>();
        String reply = "";
        if (!repo.isEmpty()) {
            for (String s : repo.keySet()){
                l.add(s);
            }
        } else {
            l.add("empty");
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
            List<Integer> i = new ArrayList<>();
            i.add(0);
            return "No key found!";
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
    public String enumKeys() throws RepException, RemoteException {
        List<String> l = new ArrayList<String>();
        String reply = "";
        if (!repo.isEmpty()) {
            for (String s : repo.keySet()){
                l.add(s);
            }
        } else {
            l.add("empty");
        }
        for (String i : l) {
            reply += " " + i + ",";
        }
        reply = reply.substring(0, (reply.length() - 1));
        return reply;
    }

    @Override
    public String enumValues(String key) throws RepException, RemoteException {
        String reply = "";
        if (!repo.containsKey(key)) {
            List<Integer> i = new ArrayList<>();
            i.add(0);
            return "No key found!";
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
        System.out.println(key);
        System.out.println(repo);
        List<Integer> values = repo.get(key);

        int sum = 0;
        for (int i = 0; i < values.size(); i++){
            sum = sum + values.get(i);
        }
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
    public int aggregate(String message) throws RepException {
        return 0;
    }
}
