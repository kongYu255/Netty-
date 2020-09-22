package com.yuy.netty.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class FileDeleteServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        if (httpObject instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) httpObject;
            if (!request.uri().startsWith("/delete")) {
                channelHandlerContext.fireChannelRead(httpObject);
            }
            if (request.method() != HttpMethod.POST) {
                sendError(channelHandlerContext, HttpResponseStatus.METHOD_NOT_ALLOWED);
                return;
            }
        }

        if (httpObject instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) httpObject;
            // 读取body部分内容
            ByteBuf content = httpContent.content();
            byte[] bytes = new byte[content.readableBytes()];
            content.readBytes(bytes);

            // 从传输过来的json中取出filePath
            String str = new String(bytes, "UTF-8");
            JSONObject json = JSONObject.parseObject(str);
            String filePath = (String) json.get("filePath");

            filePath = sanitizeUri(filePath);

            File file = new File(filePath);
            if (file == null || !file.exists()) {
                sendSuccess(channelHandlerContext, "文件已被删除");
                return;
            }
            if (file.isDirectory()) {
                sendSuccess(channelHandlerContext, "目标文件是个文件夹");
                return;
            }

            boolean delete = file.delete();
            if (delete) {
                sendSuccess(channelHandlerContext, "删除成功");
            } else {
                sendSuccess(channelHandlerContext, "删除失败");
            }
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

        if (!uri.startsWith("/src")) {
            return null;
        }
        if (!uri.startsWith("/")) {
            return null;
        }

        // File.separatorChar 获取系统文件路径的斜杠 Windows是\, Linux是/
        uri = uri.replace('/', File.separatorChar);
        if (uri.contains(File.separator + '.') || uri.contains('.' + File.separator)
                || uri.startsWith(".") || uri.endsWith(".")) {
            return null;
        }

        return System.getProperty("user.dir") + uri;
    }

    /**
     * 产生错误后发送错误消息
     * @param ctx
     * @param status
     */
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                Unpooled.copiedBuffer("Failture: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }


    /**
     * 发送成功消息
     * @param ctx
     * @param msg
     */
    private static void sendSuccess(ChannelHandlerContext ctx, String msg) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(msg + "\r\n", CharsetUtil.UTF_8));

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        ctx.close();
    }
}
