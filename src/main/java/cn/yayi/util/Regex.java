package cn.yayi.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Regex {
    public static final String IMG_TARGET = "<img[^>]*(src=){1,}[^>]*>";

    /**
     * 返回所有匹配项，没有则返回长度为0的空List集合
     * @param input
     * @param regex
     * @return
     */
    public static List<String> matches(String input, String regex) {
        ArrayList<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            String str = matcher.group();
            result.add(str);
        }

        return result;
    }

    /**
     * 获取第一个匹配项，没有返回空串("")
     * @param input
     * @param regex
     * @return
     */
    public static String match(String input, String regex) {

        List<String> matches = Regex.matches(input, regex);
        String result = matches.size() > 0 ? matches.get(0) : "";

        return result;
    }

    /**
     * 验证是否匹配
     * @param input
     * @param regex
     * @return
     */
    public static Boolean isMatch(String input, String regex) {

        List<String> matches = Regex.matches(input, regex);
        Boolean result = matches.size() > 0 ;

        return result;
    }

}
