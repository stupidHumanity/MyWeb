package cn.yayi.util;

import cn.yayi.bean.Property;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.*;

@SuppressWarnings("unused")
public class DataAccessObjectUtil {
    private Property path = new Property();
    private FileWriter fileWriter;
    private List<String> excludeColumns;
    private MySqlConnector connector;
    private Property types;
    private Property typeImports;


    public static void main(String[] args) throws Exception {

        DataAccessObjectUtil util = new DataAccessObjectUtil();
        util.setResourcepath("\\src\\main\\java");
        util.setPackage("cn.yayi");
        util.setConnection("192.168.254.76", 5433, "db_iboss_kjsc", "db_iboss", "db_iboss");
        util.excute("visit_record");
        util.dispose();
    }



    private void generateEntity(String className, List<Property> table) {


        String packageName = "package " + this.path.getString("package").concat(".entity");
        Set<String> imports = new HashSet<>();
        StringBuilder fields = new StringBuilder();
        StringBuilder getterSetter = new StringBuilder();
        for (Property column : table) {

            String type = types.getString(column.getString("type"));
            String name = column.getString("name");
            //类型引入
            imports.add(typeImports.getString(type));
            //注释
            if (column.getString("comment").length() > 0) {

                fields.append(String.format("\n    /**\n" +
                        "     *  %s\n" +
                        "     */", column.getString("comment")));
            }
            //字段
            String str = String.format("\n    private %s %s;\n", type, name);
            fields.append(str);
            //访问设置器
            getterSetter.append(String.format("\n    public void %s(%s %s) { this.%s = %s; }", "set".concat(StringUtil.upperFirst(name)), type, name, name, name));
            getterSetter.append(String.format("\n    public %s %s() { return %s; }", type, "get".concat(StringUtil.upperFirst(name)), name));
        }
        StringBuilder importStr = new StringBuilder();
        for (String e : imports) {
            importStr.append(e + "\n");
        }

        String entityText = String.format("%s;\n\n %s\n public class %s { \n%s%s \n } ", packageName, importStr.toString(), className, fields.toString(), getterSetter.toString());

        try {
            File entity = new File(this.getFileRealPath("entity", className + ".java"));
            if (entity.exists()) {
                entity.delete();
            }
            if (!entity.getParentFile().exists()) {
                entity.getParentFile().mkdirs();
            }
            entity.createNewFile();
            fileWriter = new FileWriter(entity);
            fileWriter.write(entityText);
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void generateDao(String className){
        String packageName = "package " + this.path.getString("package").concat(".dao"); 

    }

    public void setConnection(String ip, int port, String dbName, String user, String pwd) {
        try {
            connector=new MySqlConnector();
            connector.getConnection(ip, port, dbName, user, pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void excute(String tableName) throws Exception {

        String className = StringUtil.toHumpString(tableName);
        className = StringUtil.upperFirst(className);
        List<Property> table = connector.getDataTableInfo(tableName, this.excludeColumns);
        generateEntity(className, table);

    }
    public  void dispose(){
        connector.releaseConnection();
    }

    public DataAccessObjectUtil() {
        String path = new File("").getAbsolutePath();
        this.path.put("project", path);
        this.path.put("entity", "\\entity");
        this.path.put("dao", "\\dao");
        this.path.put("mapper", "\\dao\\mapper");

        types = new Property();
        types.put("bigint", "Long");
        types.put("int", "Integer");
        types.put("varchar", "String");
        types.put("datetime", "Date");

        typeImports = new Property();
        typeImports.put("Long", "import java.lang.Long;");
        typeImports.put("Integer", "import java.lang.Integer;");
        typeImports.put("String", "import java.lang.String;");
        typeImports.put("Date", "import java.util.Date;");

        excludeColumns = new ArrayList<>();


        MySqlConnector connector = new MySqlConnector();
    }

    public void setPackage(String path) {
        this.path.put("package", path);
    }

    public void setResourcepath(String path) {
        this.path.put("src", path);
    }

    public String getFileRealPath(String type, String fileName) throws Exception {
        ArrayList arrayList = new ArrayList() {{
            add("dao");
            add("mapper");
            add("entity");
        }};
        if (arrayList.contains(type)) {
            return String.format("%s%s\\%s%s\\%s", this.path.getString("project"), this.path.getString("src"), this.path.getString("package").replace(".", "\\"), this.path.getString(type), fileName);
        } else {
            throw new Exception("不合要求的参数值！");
        }
    }

    public void addExclude(String... columns) {
        this.excludeColumns.addAll(Arrays.asList(columns));
    }
}

class MySqlConnector {
    /**
     * Connection对象
     */
    Connection con;
    /**
     * 驱动程序名
     */
    String driver = "com.mysql.jdbc.Driver";
    /**
     * 链接字符串模板
     */
    String url = "jdbc:mysql://${ip}:${port}/${dbName}";

    public void getConnection(String ip, int port, String dbName, String user, String pwd) throws Exception {
        Class.forName(driver);
        //1.getConnection()方法，连接MySQL数据库！！
        String conStr = this.url;
        conStr = conStr.replace("${ip}", ip);
        conStr = conStr.replace("${port}", port + "");
        conStr = conStr.replace("${dbName}", dbName);
        con = DriverManager.getConnection(conStr, user, pwd);
        if (!con.isClosed()) {
            System.out.println("Succeeded connecting to the Database!");
        }
    }


    public List<Property> getDataTableInfo(String tableName, List<String> excludes) throws Exception {

        List<Property> resultList = new ArrayList<>();

        String sql = String.format("select column_name,column_comment,data_type from information_schema.columns where table_name='%s'", tableName);
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        Property column;
        while (rs.next()) {
            String columnName = rs.getString("column_name");
            if (excludes.contains(columnName)) {
                continue;
            }
            column = new Property();
            column.put("column", columnName);
            column.put("type", rs.getString("data_type"));
            column.put("comment", rs.getString("column_comment"));
            column.put("name", StringUtil.toHumpString(column.getString("column")));
            resultList.add(column);
        }

        return resultList;
    }

    public void releaseConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
