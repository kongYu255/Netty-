package com.yuy.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class FileUploadServer {


    public static void main(String[] args) throws InterruptedException {
        //声明两个多线程事件循环器
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //声明nio服务启动类
        ServerBootstrap serverBootstrap = new ServerBootstrap ();
        serverBootstrap.group(bossGroup, workerGroup).
                channel(NioServerSocketChannel.class).
                option(ChannelOption.SO_BACKLOG, 1024).
                childHandler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                System.out.println("有客户端连接上来:"+ch.localAddress().toString());
                ch.pipeline().addLast(new ObjectEncoder());
                ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
                ch.pipeline().addLast(new FileUploadServerHandler());
            }
        });
        ChannelFuture f = serverBootstrap.bind(8080).sync();//邦定端口并启动
        System.out.println("file server 等待连接：");
        f.channel().closeFuture().sync();
    }
}
