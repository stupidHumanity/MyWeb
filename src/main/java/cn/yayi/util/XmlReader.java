package cn.yayi.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

public class XmlReader {
	private static SAXReader reader;
	private static Document document;

	@SuppressWarnings("rawtypes")
	public static List<Map> select(String pattern) {
		List<Map> result = new ArrayList<Map>();
		List list = document.selectNodes(pattern);
		for (Object obj : list) {
			if (obj instanceof DefaultElement) {
				DefaultElement node = (DefaultElement) obj;
				List attrs = node.attributes();
				for (Object object : attrs) {

					if (object instanceof DefaultAttribute) {
						HashMap<String, String> nodeMap = new HashMap<String, String>();
						DefaultAttribute attr = (DefaultAttribute) object;
						String key = attr.getName();
						String value = attr.getValue();
						nodeMap.put(key, value);
						result.add(nodeMap);
					}
				}
			}
		}
		return result;

	}

	public static void load(String path) {
		if (reader == null) {
			reader = new SAXReader();
		}
		try {
			document = reader.read(path);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

}
