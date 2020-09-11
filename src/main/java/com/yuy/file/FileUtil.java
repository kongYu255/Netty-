package com.yuy.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

public class FileUtil {

    /**
     * 往响应体中装入文件流
     * @param response
     * @param filePath
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
                // 如果是文件，直接在浏览器上显示
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/" + fileType);
            }
            else if ("pdf".contains(fileType)) {
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/pdf");
            }
            else {
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "multipart/form-data");
            }

            int len = 0;
            byte[] buf = new byte[(int) file.length()];
            in.read(buf);
            response.content().writeBytes(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
