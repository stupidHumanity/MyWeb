package cn.yayi.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class MapperReader {
    private Document doc;
    private SAXReader reader = new SAXReader();
    private Class clazz;

    public void test(Class clazz) throws Exception {
        this.clazz = clazz;
        String mapperFilePath = Path.mapper + clazz.getSimpleName() + "Mapper.xml";
        File mapperFile = new File(mapperFilePath);
        doc = mapperFile.exists() ? reader.read(mapperFile) : CreateNewMapper(mapperFile, clazz);
        //query
        Element query;
        List<Element> list=doc.selectNodes("//select[@id='query']");
        if(list.size()==0){
            query=doc.getRootElement().addElement("select");
            query.addAttribute("id","query");
            query.addAttribute("parameterType","java.lang.Map");
            query.addAttribute("resultType",clazz.getName());
        }else{
            query=list.get(0);
        }
        StringBuilder sb=new StringBuilder();



//        Element select = doc.getRootElement().addElement("select");
//        select.addAttribute("id", "query");
//        select.addText("select * from tableName");
        output();


    }

    private void output() throws Exception {
        OutputFormat xmlFormat = new OutputFormat();
        xmlFormat.setEncoding("utf-8");
        // 设置换行
        xmlFormat.setNewlines(true);
        // 生成缩进
        xmlFormat.setIndent(true);
        // 使用4个空格进行缩进, 可以兼容文本编辑器
        xmlFormat.setIndent("    ");

        FileOutputStream os = new FileOutputStream(doc.getName());
        XMLWriter writer = new XMLWriter(os, xmlFormat);
        writer.write(doc);
        writer.close();
        os.close();


    }


    private Document CreateNewMapper(File mapper, Class clazz) throws Exception {
        Document document = DocumentHelper.createDocument();
        document.setName(mapper.getAbsolutePath());
        Element root = document.addElement("mapper");
        document.addDocType("mapper", "-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd", null);
        root.addAttribute("namespace",  Package.dao+"."+clazz.getSimpleName()+"Dao");

        return document;
    }

}

class Path {
    public static String mapper = "D:\\ideaWorkSpace\\MyWeb\\src\\main\\java\\cn\\yayi\\dao\\mapper\\";
    public static String dao = "D:\\ideaWorkSpace\\MyWeb\\src\\main\\java\\cn\\yayi\\dao\\";
    public static String entity = "D:\\ideaWorkSpace\\MyWeb\\src\\main\\java\\cn\\yayi\\entity\\";

}

class Package{
    public static String base="cn.yayi";
    public static String mapper = base+".dao.mapper";
    public static String dao = base+".dao";
    public static String entity =base+".entity";

}