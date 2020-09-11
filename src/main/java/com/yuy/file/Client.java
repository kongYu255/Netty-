package com.yuy.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class Client {

    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:8888/src/save");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            int code = httpURLConnection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                OutputStream outputStream = httpURLConnection.getOutputStream();
                File file = new File("/home/santi/Desktop/123.txt");

//                InputStream inputStream = httpURLConnection.getInputStream();
//                OutputStream outputStream = new FileOutputStream(new File("/home/santi/Desktop/123.txt"));
//                byte[] bytes = new byte[308];
//                inputStream.read(bytes);
//                outputStream.write(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
