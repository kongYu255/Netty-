package com.yuy.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            ((ServerBootstrap)serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)).childHandler(new ServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            if (channelFuture.isSuccess()) {
                System.out.println("绑定成功");
            }

            channelFuture.channel().closeFuture().sync();
        } catch (Exception var8) {
            var8.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
