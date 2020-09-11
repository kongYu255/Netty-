package com.yuy.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    public ServerInitializer() {
    }

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        pipeline.addLast("MyHttpServerHandler", new HttpServerHandler());
    }
}