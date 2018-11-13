package cn.yayi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static Pattern characterRegex= Pattern.compile("[a-zA-Z]+");;

    public static String toHumpString(String input){
        StringBuilder sb=new StringBuilder();


        Matcher matcher = characterRegex.matcher(input);
        while (matcher.find()) {
            String str = matcher.group();
            str=upperFirst(str);
            sb.append(str);
        }
        return lowerFirst(sb.toString());
    }

    public static String upperFirst(String input){

        char[] strChar=input.toCharArray();
        strChar[0]-=32;
        return String.valueOf(strChar);
    }
    public static String lowerFirst(String input){

        char[] strChar=input.toCharArray();
        strChar[0]+=32;
        return String.valueOf(strChar);
    }
}
