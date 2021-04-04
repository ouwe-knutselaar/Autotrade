package autotrade.pojo;

import java.util.*;

public class Table<T extends Number>{

    private Map<String,T> internalTable = new HashMap<>();

    public void create(String key,T value){
        internalTable.put(key,value);
    }

    public T read(String key)    {
        return internalTable.get(key);
    }

    public void delete(String key){
        internalTable.remove(key);
    }

    public Map<String,T> readAll()    {
        Map<String,T> workList=new HashMap<>();
        internalTable.forEach((K,V) -> workList.put(K,V));
        return workList;
    }

    public Map<String,T> readAllSortedDesc(){
        LinkedHashMap<String,T> workList = new LinkedHashMap<>();
        internalTable.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, T >>() {
                    @Override
                    public int compare(Map.Entry<String, T> o1, Map.Entry<String, T> o2) {
                        if(o1.getValue().doubleValue()<o2.getValue().doubleValue())return -1;
                        if(o1.getValue().doubleValue()>o2.getValue().doubleValue())return 1;
                        return 0;
                    }
                })
                .forEach(entry -> workList.put(entry.getKey(),entry.getValue()));

        return workList;
    }

    public Map<String,T> readAllSortedAsc(){
        LinkedHashMap<String,T> workList = new LinkedHashMap<>();
        internalTable.entrySet()
                .stream()
                .sorted(new Comparator<Map.Entry<String, T >>() {
                    @Override
                    public int compare(Map.Entry<String, T> o1, Map.Entry<String, T> o2) {
                        if(o1.getValue().doubleValue()<o2.getValue().doubleValue())return 1;
                        if(o1.getValue().doubleValue()>o2.getValue().doubleValue())return -1;
                        return 0;
                    }
                })
                .forEach(entry -> workList.put(entry.getKey(),entry.getValue()));

        return workList;
    }

    @Override
    public String toString(){
        StringBuilder output =new StringBuilder("{");
        internalTable.forEach((K,V) -> output.append("\"").append(K).append("\":\"").append(V).append("\","));
        output.deleteCharAt(output.length()-1).append("}");
        return output.toString();
    }

    public int size() {
        return internalTable.size();
    }
}
