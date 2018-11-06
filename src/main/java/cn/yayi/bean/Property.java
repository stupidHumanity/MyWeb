package cn.yayi.bean;

import java.util.HashMap;

/**
 * @author DGG-S27-D-20
 */
@SuppressWarnings("unused")
public class Property extends HashMap<String, Object> {


    public String getString(String key, String def) {
        if (containsKey(key) && get(key) != null) {
            def = get(key).toString();
        }
        return def;
    }

    public String getString(String key) {
        return getString(key, "");
    }
}
