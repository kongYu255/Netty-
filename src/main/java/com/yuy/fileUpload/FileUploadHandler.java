package com.yuy.fileUpload;

import com.sun.javafx.scene.shape.PathUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final String uploadUrl = "/upload";

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private HttpRequest httpRequest;

    private HttpPostRequestDecoder decoder;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            this.httpRequest = (HttpRequest) msg;
            URI uri = new URI(httpRequest.uri());
            System.out.println(uri);
            uriRoute(channelHandlerContext, uri.getPath());
        }

        if (decoder != null) {
            if (msg instanceof HttpContent) {
                decoder.offer((HttpContent) msg);
                readFileIntoDisk();
            }
        }
    }


    private void uriRoute(ChannelHandlerContext ctx, String uri) {
        StringBuilder urlResponse = new StringBuilder();
        if (uri.startsWith(uploadUrl)) {
            decoder = new HttpPostRequestDecoder(factory, httpRequest);
            return;
        }
    }

    private void readFileIntoDisk() throws IOException {
        while (decoder.hasNext()) {
            InterfaceHttpData data = decoder.next();
            if (data != null) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        fileUpload.isInMemory();
                        fileUpload.renameTo(new File("/opt/testProject/NettyTest/src/" + fileUpload.getFilename()));
                        decoder.removeHttpDataFromClean(fileUpload);
                    }
                }
            }
        }
    }
}
