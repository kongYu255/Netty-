package com.yuy.fileServer;

import com.yuy.fileServer.FileUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FileDownLoadHandler extends SimpleChannelInboundHandler<HttpObject> {

    private final String url;

    private static final Pattern is_url = Pattern.compile(".*[<>&\"].*");

    public FileDownLoadHandler(String url) {
        this.url = url;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject msg) throws Exception {
        HttpRequest request = null;
        if (msg instanceof HttpRequest) {
            request = (HttpRequest) msg;
        } else {
            sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }


        if (!request.decoderResult().isSuccess()) {
            sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        if (request.method() != HttpMethod.GET) {
            sendError(channelHandlerContext, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String uri = request.uri();
        final String path = sanitizeUri(uri);

        // 浏览器请求会再次请求资源文件，如果是请求浏览器图标资源文件的话，直接返回
        if (uri.contains("/favicon.ico")) {
            return;
        }

        if (path == null || path.equals("")) {
            sendError(channelHandlerContext, HttpResponseStatus.FORBIDDEN);
        }

        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND);
            return;
        }

        if (file.isDirectory()) {
            sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND);
            return;
        }

        if (!file.isFile()) {
            sendError(channelHandlerContext, HttpResponseStatus.FORBIDDEN);
            return;
        }

//        // 如果是请求上传文件，则进入下一个处理器处理
//        if (uri.contains("/upload")) {
//            channelHandlerContext.fireChannelRead(request);
//        }

        sendSuccess(channelHandlerContext, file, request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 解析uri，拼接成一个带本地的文件路径
     * @param uri
     * @return
     */
    private String sanitizeUri(String uri) {
        try {
            //uri解码
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                unsupportedEncodingException.printStackTrace();
                throw new Error("uri错误");
            }
        }

        if (!uri.startsWith(url)) {
            return null;
        }
        if (!uri.startsWith("/")) {
            return null;
        }

        // File.separatorChar 获取系统文件路径的斜杠 Windows是\, Linux是/
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator)
                || uri.startsWith(".") || uri.endsWith(".") || is_url.matcher(uri).matches()) {
            return null;
        }

        return System.getProperty("user.dir") + uri;
    }


    private static void sendSuccess(ChannelHandlerContext ctx, File file, HttpRequest request) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        Long fileLength = file.length();
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
        FileUtil.responseFileStream(response, file);
        if (HttpUtil.isKeepAlive(request)) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        Map<String, Object> content = new HashMap<String, Object>();
        content.put("fileName", "123.txt");
        byte[] bytes = null;
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bo);
            oos.writeObject(content);
            bytes = bo.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bo.close();
                oos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        response.content().writeBytes(bytes);

        ChannelFuture channelFuture = ctx.writeAndFlush(response);

        // 如果不是长连接，监听关闭通道
        if (!HttpUtil.isKeepAlive(request)) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
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
