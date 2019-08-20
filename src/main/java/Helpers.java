import java.util.HashMap;
import java.util.List;

public class Helpers {

    static <T> String toJSONArray(List<T> list, String joint) {
        String output = "[";
        for (T obj : list) {
            output = "\"" + output.concat(obj.toString() + "\"" + joint);
        }
        output = output.substring(0, output.length() - 1);
        output = output.concat("]");
        return output;
    }

    static <T> String getJSONArrayFromHashMap(HashMap<T, Integer> map, String keyName, String valueName) {
        String output = "[";
        if (map.size() == 0) {
            output = "[]";
        } else {
            for (HashMap.Entry<T, Integer> entry : map.entrySet()) {
                output = output.concat("{\"" + keyName + "\":\"" + entry.getKey().toString() + "\", \"" + valueName + "\":" + entry.getValue()) + "},";
            }
            output = output.substring(0, output.length() - 1);
            output = output.concat("]");
        }
        return output;
    }
}
