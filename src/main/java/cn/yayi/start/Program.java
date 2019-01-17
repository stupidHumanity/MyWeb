package cn.yayi.start;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.yayi.util.PoiUtil;
import org.apache.poi.xwpf.usermodel.*;

/**
 * Created by Administrator on 2018/2/27 0027.
 */
public class Program {
    public static void main(String[] args) throws Exception {

        doTest();
    }


    private static void doPoiTest() throws Exception {
        Map<String,String> para=new HashMap(){{
           put("title","标题");
           put("message","消息");
           put("table","表格");
           put("auhtoer","zyw");
        }};
        PoiUtil.doReplace("D:\\ideaWorkSpace\\MyWeb\\src\\main\\resources\\tplt.docx",para,"C:\\Users\\DGG-S27-D-20\\Desktop\\tplt.docx");

    }
private static void doTest(){
    Map<String,Object> para = new HashMap(){{
        put("orgLevel",1);
        put("orgId",2);

    }};
    Iterator<String> it=  para.keySet().iterator();
    while (it.hasNext()){
        String key=it.next();
        if(para.get(key)==null) para.remove(key);
    }
    System.out.println(para.toString());
}





}

