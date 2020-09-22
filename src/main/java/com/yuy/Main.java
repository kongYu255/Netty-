package com.yuy;

import com.alibaba.fastjson.JSONObject;
import io.netty.util.CharsetUtil;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        File file1 = new File("C:\\Users\\余湧\\Desktop\\232112.png");
        File file2 = new File("C:\\Users\\余湧\\Desktop\\232112 - 副本.png");
        try {
            System.out.println(DigestUtils.md5Hex(new FileInputStream(file1)));
            System.out.println(DigestUtils.md5Hex(new FileInputStream(file2)));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            URL url = new URL("http://127.0.0.1:8080/delete");
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("POST");
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//            connection.setRequestProperty("Charsert", "UTF-8");
//            connection.setRequestProperty("Content-Type", "application/json");
//
//            OutputStream outputStream = connection.getOutputStream();
//            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
//            JSONObject object = new JSONObject();
//            object.put("filePath", "/src/upload/Colorful-Abstraction01.jpg");
//            dataOutputStream.write(object.toJSONString().getBytes());
//            outputStream.close();
//            dataOutputStream.close();
//
//            int code = connection.getResponseCode();
//            if (code == HttpURLConnection.HTTP_OK) {
//                // 接受返回信息
//                InputStream inputStream = connection.getInputStream();
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                StringBuffer stringBuffer = new StringBuffer();
//                String str = null;
//                while ((str = bufferedReader.readLine()) != null) {
//                    stringBuffer.append(str);
//                }
//                System.out.println(stringBuffer.toString());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
