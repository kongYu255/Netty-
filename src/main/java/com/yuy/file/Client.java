package com.yuy.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class Client {

    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:8888/src/123.txt");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                Map content = (Map) httpURLConnection.getContent();
                System.out.println(content.get("fileName"));
                OutputStream outputStream = new FileOutputStream(new File("/home/santi/Desktop/123.txt"));
                byte[] bytes = new byte[308];
                inputStream.read(bytes);
                outputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
