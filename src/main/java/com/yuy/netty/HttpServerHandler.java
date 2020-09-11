package com.yuy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if (httpObject instanceof HttpRequest) {
            System.out.println("pipeline hashcode " + channelHandlerContext.pipeline().hashCode());
            System.out.println("类型 " + httpObject.getClass());
            System.out.println("客户端地址 " + channelHandlerContext.channel().remoteAddress());
            HttpRequest httpRequest = (HttpRequest)httpObject;
            URI uri = new URI(httpRequest.uri());
            if ("/favicon.ico".equals(uri.getPath())) {
                return;
            }

            ByteBuf content = Unpooled.copiedBuffer("hello，我是服务器", CharsetUtil.UTF_16);
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
            channelHandlerContext.writeAndFlush(httpResponse);
        }

    }
}
