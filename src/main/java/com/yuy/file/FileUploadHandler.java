package com.yuy.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUploadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()) {
            sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        String uri = request.uri();
        ByteBuf content = request.content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);

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
