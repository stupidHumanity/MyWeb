package cn.yayi.util;


import cn.yayi.annotation.Text;
import cn.yayi.constant.UserState;
import org.apache.commons.collections4.map.LinkedMap;

public class ConstantUtil {
    public static LinkedMap getMap(Class<?> clazz) {
        LinkedMap retMap = new LinkedMap();

        try {
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();

            for (java.lang.reflect.Field field : fields) {
                if (field.isAnnotationPresent(Text.class)) {
                    Text annotation = field.getAnnotation(Text.class);
                    String key = String.valueOf(field.get(null));
                    String text = annotation.value();
                    retMap.put(key, text);
                }
            }
        } catch (Exception e) {
        }
        return retMap;
    }

    public static void main(String[] args) {

        ConstantUtil.getMap(UserState.class);
    }
}
