package nl.wesleydev.wesleydev.helpers;

import java.util.ArrayList;
import java.util.List;

public class ListHelper {

    /**
     * @return Filtered list with String items that starts with the given value.
     * If value is null or empty it returns the given list.
     */
    public static List<String> getItemsStartWithValue(List<String> list, String value) {
        if (value == null || value.equals("")) return list;

        List<String> returnList = new ArrayList<String>();
        for (String item : list) {
            if (item.startsWith(value.toLowerCase())) {
                returnList.add(item);
            }
        }
        return returnList;
    }

    /**
     * @return Filtered list with String items that contains the given value.
     * If value is null or empty it returns the given list.
     */
    public static List<String> getItemsContainValue(List<String> list, String value) {
        if (value == null || value.equals("")) return list;

        List<String> returnList = new ArrayList<String>();
        for (String item : list) {
            if (item.contains(value.toLowerCase())) {
                returnList.add(item);
            }
        }
        return returnList;
    }

}
