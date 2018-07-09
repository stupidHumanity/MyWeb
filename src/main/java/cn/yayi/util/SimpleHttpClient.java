package cn.yayi.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("unused")
public class SimpleHttpClient {

    private HttpRequestBase httpRequest;
    private CloseableHttpClient httpClient;
    private HttpEntity responseEntity;

    private String charset = "utf-8";

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    /**
     * 获取请求响应内容
     *
     * @return
     */
    public String getResponseString() {
        String result = "";


        try {
            getResponseEntity();
            if (this.responseEntity != null) result = EntityUtils.toString(this.responseEntity, this.charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 读取为文件，写到指定路径
     * @param path
     */
    public void getResponseFile(String path) {
        byte[] data = getResponseByteArray();
        if (data == null) return;
        File file = new File(path);
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置请求地址和类型
     *
     * @param url
     * @param type
     */
    public void setConnection(String url, String type) {
        initHttpCilent();
        url = urlCheck(url);
        if (type != null && type.trim().toUpperCase().equals("POST")) {
            httpRequest = new HttpPost(url);
        } else {
            httpRequest = new HttpGet(url);
        }
    }

    /**
     * 设置请求地址，请求类型默认为GET
     *
     * @param url
     */
    public void setConnection(String url) {

        setConnection(url, "GET");
    }

    /**
     * 设置请求参数(仅post请求)
     *
     * @param para
     * @throws Exception
     */
    public void setParameter(Map<String, String> para) throws Exception {

        if (httpRequest == null) {
            throw new Exception("httpRequest hasn't init.");
        } else if (httpRequest instanceof HttpGet) {
            throw new Exception("post request needn't set parameter.");
        }
        if (para != null) return;
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        Iterator<Entry<String, String>> it = para.entrySet().iterator();
        while (it.hasNext()) {

            Entry<String, String> entry = it.next();
            BasicNameValuePair pair = new BasicNameValuePair(
                    entry.getKey(), entry.getValue());
            list.add(pair);
        }
        if (list.size() > 0) {
            UrlEncodedFormEntity entity;
            try {
                entity = new UrlEncodedFormEntity(list, this.charset);
                ((HttpPost) httpRequest).setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }

    //私有方法

    /**
     * 初始化成员
     */
    private void initHttpCilent() {
        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
    }

    /**
     * 支持url省略协议开头
     *
     * @param url
     * @return
     */
    private String urlCheck(String url) {
        if (!url.startsWith("http://")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * 获取响应实体
     */
    private void getResponseEntity() {
        HttpResponse response;
        try {
            response = httpClient.execute(httpRequest);
            if (response != null) {
                responseEntity = response.getEntity();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取响应字符流
     *
     * @return
     */
    private byte[] getResponseByteArray() {
        byte[] result = null;
        HttpResponse response;
        try {
            getResponseEntity();

            if (this.responseEntity != null) {
                result = EntityUtils.toByteArray(this.responseEntity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
