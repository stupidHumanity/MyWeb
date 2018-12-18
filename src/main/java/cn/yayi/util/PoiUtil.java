package cn.yayi.util;

import org.apache.poi.xwpf.usermodel.*;

import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PoiUtil {

    private static final Pattern pattern = Pattern.compile("\\$\\{.+\\}", Pattern.CASE_INSENSITIVE);
    private static void doReplace(XWPFParagraph paragraph, Map<String, String> para) throws Exception {
        List<XWPFRun> runList = paragraph.getRuns();

        for (int i = 0; i < runList.size(); i++) {
            XWPFRun run = runList.get(i);
            String text = run.text();
            Matcher matcher;
            while ((matcher = pattern.matcher(text)).find()) {
                String key = matcher.group(0);
                key=key.substring(2,key.length()-1);
                if (para.containsKey(key)) {
                    text = matcher.replaceFirst(para.get(key));
                } else {
                    throw new Exception("未找到" +key + "对应的替换值");
                }
            }
            if (!run.text().equals(text)) {
                run.setText(text,0);
            }
        }
    }
    private static XWPFDocument doReplace(XWPFDocument document, Map para) throws Exception {
        Iterator<IBodyElement> iterator = document.getBodyElementsIterator();
        while (iterator.hasNext()) {
            IBodyElement element = iterator.next();


            if (element instanceof XWPFParagraph) {
                doReplace((XWPFParagraph) element, para);
            } else if (element instanceof XWPFTable) {
                List<XWPFTableRow> rowList = ((XWPFTable) element).getRows();
                for (XWPFTableRow row : rowList) {
                    List<XWPFTableCell> cellList = row.getTableCells();
                    for (XWPFTableCell cell : cellList) {
                        List<XWPFParagraph> paragraphList = cell.getParagraphs();
                        for (XWPFParagraph paragraph : paragraphList) {
                            doReplace(paragraph, para);
                        }
                    }

                }
            }

        }

        return document;
    }

    /**
     * 　替换word中${key}占位符，其中key对应value应在para中指定
     * @param resource 模板路径
     * @param para 替换键值对
     * @param target 生成位置
     */
    public static void doReplace(String resource, Map<String,String> para,String target){
        try {
            InputStream inputStream = new FileInputStream(resource);
            XWPFDocument hwpfDocument = new XWPFDocument(inputStream);

            doReplace(hwpfDocument, para);
            inputStream.close();

            File file = new File(target);
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream outputStream = new FileOutputStream(file);
            hwpfDocument.write(outputStream);
            outputStream.close();
        }
        catch (Exception e){}


    }

    public static void doExport() throws Exception{
        throw new Exception("not implement ,yet!");
    }
}
