package com.yuy.netty.server;

import com.yuy.netty.FileUploadFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUploadServerHandler extends SimpleChannelInboundHandler<Object> {

    private int byteRead;

    private volatile int start = 0;

    private String fileDir = "/home/santi/Desktop";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if (o instanceof FileUploadFile) {
            FileUploadFile ef = (FileUploadFile) o;
            byte[] bytes = ef.getBytes();

            byteRead = ef.getEndPos();

            String md5 = ef.getFile_md5();
            String path = null;
            if (ef.getFilePath() == null || ef.getFilePath().equals("")) {
                path = fileDir + File.separator;
            } else {
                path = ef.getFilePath() + File.separator;
            }
            // 查看目录是否存在
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path += md5;

            File file = new File(path);
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


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
