package com.yuy.file;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class Client {

    public static void main(String[] args) {
        try {
            File file = new File("/home/santi/Desktop/123.txt");
            URL url = new URL("http://localhost:8888/src/upload");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Charsert", "UTF-8");
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Content-Type", "multipart/form-data;file="+ file.getName());
            connection.setRequestProperty("fileName",file.getName());
            // 上传文件
            OutputStream outputStream = connection.getOutputStream();
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
            byte[] bytes = new byte[(int) file.length() - 1];
            if (dataInputStream.read(bytes) != -1) {
                outputStream.write(Integer.parseInt("1234"));
            }
            connection.connect();

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                // 接受返回信息
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuffer.append(str);
                }
                System.out.println(stringBuffer.toString());
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
