package com.yuy.fileServer;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.io.File;
import java.io.FileInputStream;

public class FileUtil {

    /**
     * 往响应体中装入文件流
     * @param response
     * @param file
     */
    public static void responseFileStream(FullHttpResponse response, File file) {
        String filePath = file.getPath();
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            String[] dir = filePath.split("/");
            String fileName = dir[dir.length - 1];
            String[] type = filePath.split("[.]");
            String fileType = type[type.length - 1];

            if ("jpg,jepg,gif,png".contains(fileType)) {
                // 如果是图，直接在浏览器上显示
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/" + fileType);
            }
            else if ("pdf".contains(fileType)) {
                // 如果是pdf，直接在浏览器上显示
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/pdf");
            }
            else {
                // 如果是其他的，让浏览器解析
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "multipart/form-data");
            }

            byte[] buf = new byte[(int) file.length()];
            in.read(buf);
            response.content().writeBytes(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
