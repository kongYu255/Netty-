package com.yuy.netty.server;

import com.yuy.netty.FileUploadFile;
import com.yuy.netty.util.CheckFileTypeUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUploadServerHandler extends SimpleChannelInboundHandler<Object> {

    private int byteRead;

    private volatile int start = 0;

    private String DEFAULT_FILE_DIR = System.getProperty("user.dir") + "/src";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof FileUploadFile) {
            FileUploadFile ef = (FileUploadFile) o;
            byte[] bytes = ef.getBytes();

            byteRead = ef.getEndPos();

            String md5 = ef.getFile_md5();
            String path = null;
            if (ef.getFilePath() == null || ef.getFilePath().equals("")) {
                path = DEFAULT_FILE_DIR + File.separator;
            } else {
                path = DEFAULT_FILE_DIR + ef.getFilePath() + File.separator;
            }
            // 根据不同系统更换路径
            path.replace('/', File.separatorChar);

            // 查看目录是否存在
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += md5;

            File file = new File(path);
            String fileType = "success";
            if (ef.getStarPos() == 0) {
                fileType = CheckFileTypeUtil.getFileTypeByBytes(ef.getBytes());
                System.out.println(fileType);
            }
            if (fileType == null) {
                channelHandlerContext.writeAndFlush("该文件类型禁止上传!");
            } else {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.seek(start);
                randomAccessFile.write(bytes);
                start = start + byteRead;
                if (byteRead > 0) {
                    channelHandlerContext.writeAndFlush(start);
                } else {
                    randomAccessFile.close();
                    channelHandlerContext.close();
                }
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 产生错误后发送错误消息
     * @param ctx
     * @param status
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("该文件类型禁止被上传" + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
