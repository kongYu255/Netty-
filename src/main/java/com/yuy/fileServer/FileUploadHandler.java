package com.yuy.fileServer;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class FileUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final String uploadUrl = "/upload";

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private HttpRequest httpRequest;

    private HttpPostRequestDecoder decoder;

    private boolean responseFlag = false;   // 表示是否回复

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

        // 如果不是post，进入下一个处理器处理
        if (httpRequest.method() != HttpMethod.POST) {
            channelHandlerContext.fireChannelRead(httpRequest);
            return;
        }

        // 如果已经发送错误信息了，就直接返回
        if (responseFlag) {
            return;
        }

        // 浏览器会再次请求资源文件，如果是请求浏览器图标资源文件的话，不处理直接返回
        if (httpRequest.uri().contains("favicon.ico")) {
            return;
        }

        if (decoder != null) {
            if (msg instanceof HttpContent) {
                decoder.offer((HttpContent) msg);
                readFileIntoDisk();
            }
        }

        if (msg instanceof LastHttpContent) {
            HttpResponse response = getResponse();
            channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE).sync();
        }
    }

    private FullHttpResponse getResponse() {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        String str = "OK";
        response.content().writeBytes(Unpooled.copiedBuffer(str.getBytes()));
        return response;
    }


    private void uriRoute(ChannelHandlerContext ctx, String uri) {
        if (uri.startsWith(uploadUrl)) {
            decoder = new HttpPostRequestDecoder(factory, httpRequest);
            return;
        }
        if (!httpRequest.decoderResult().isSuccess()) {
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            responseFlag = true;
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

    /**
     * 产生错误后发送错误消息
     * @param ctx
     * @param status
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failture: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
