package cn.yayi.start;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yayi.bean.Property;
import cn.yayi.bean.User;
import cn.yayi.service.GenericService;
import cn.yayi.service.impl.PropertyService;
import cn.yayi.util.PoiUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xwpf.usermodel.*;

/**
 * Created by Administrator on 2018/2/27 0027.
 */
public class Program {
    public static void main(String[] args) throws Exception {

        doTest();
    }


    private static void doTest() throws Exception {

        Pattern pattern= Pattern.compile();
        String[] patterns=new String[]{"^[a-zA-Z]+$","(?<=>)[a-zA-Z]+","(?<=<)[a-zA-Z]+","(?<=%)[a-zA-Z]+(?!%)"};
        Matcher matcher=pattern.matcher( "%name");
        if(matcher.find()){
            String key=matcher.group();
        }
    }



    private static void doTest3() throws Exception {

        User user=new User();
        user.setName("admin");
        Field field=user.getClass().getSuperclass().getDeclaredField("id");
        field.setAccessible(true);

        field.set(user,9527L);


    }


    private static void doTest2() {

        PropertyService service=new PropertyService();
        service.getEntityClass();


    }


    private static void doTest1() {
        Map<String, Object> para = new HashMap() {{
            put("orgLevel", 1);
            put("orgId", 2);

        }};
        Iterator<String> it = para.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (para.get(key) == null) para.remove(key);
        }
        System.out.println(para.toString());
    }


}

