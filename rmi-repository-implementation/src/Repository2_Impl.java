import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Repository2_Impl extends UnicastRemoteObject implements IRepository{
    private static HashMap<String, List<Integer>> repo = new HashMap<>();

    protected Repository2_Impl() throws RemoteException {
        super();
    }

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
        return 0;
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
        if (reply != " ,") {
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
        if (values == null) throw new RepException("Key does not exist!!");

        else {
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
