import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Repository2_Impl extends UnicastRemoteObject implements IRepository{
    private HashMap<String, List<Integer>> repo;

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
        return 0;
    }

    @Override
    public int min(String key) throws RepException {
        return 0;
    }

    @Override
    public int max(String key) throws RepException {
        return 0;
    }

    @Override
    public int aggregate(String message) throws RemoteException, NotBoundException, RepException {
        return 0;
    }
}
