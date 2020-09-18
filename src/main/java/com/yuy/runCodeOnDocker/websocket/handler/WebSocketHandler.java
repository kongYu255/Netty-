package com.yuy.runCodeOnDocker.websocket.handler;

import com.yuy.docker.DockerUtil;
import com.yuy.entity.CodeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private DockerUtil dockerUtil = new DockerUtil();

    /**
     * 处理WebSocket请求(代码类型写死了Java)
     * TODO
     * @param channelHandlerContext
     * @param textWebSocketFrame
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String content = textWebSocketFrame.text();
        CodeEnum code = CodeEnum.JAVA;
        long startTime = System.currentTimeMillis();
        dockerUtil.exec(channelHandlerContext, code, content, null);
        long endTime = System.currentTimeMillis();
        System.out.println((endTime - startTime) + "毫秒" + " websocket");
    }
}
