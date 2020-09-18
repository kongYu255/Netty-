package com.yuy.runCodeOnDocker.http.handler;

import com.yuy.controller.CodeRunController;
import com.yuy.entity.Result;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            String uri = request.uri();

            if(uri.startsWith("/codeRun")) {
                long startTime = System.currentTimeMillis();
                Result result = new CodeRunController().handler(request);
                long endTime = System.currentTimeMillis();
                System.out.println("运行代码花费: " + (endTime - startTime) + "毫秒" + " http");
                responseResult(ctx, request, result);
            }
            else if(uri.startsWith("/saveCode")) {
                long startTime = System.currentTimeMillis();
                Result result = new CodeRunController().saveCode(request);
                long endTime = System.currentTimeMillis();
                System.out.println("保存代码花费: " + (endTime - startTime) + "毫秒" + " http");
                responseResult(ctx, request, result);
            }
            else if(uri.startsWith("/getCode")) {
                long startTime = System.currentTimeMillis();
                Result result = new CodeRunController().getCode(request);
                long endTime = System.currentTimeMillis();
                System.out.println("查询代码花费: " + (endTime - startTime) + "毫秒" + " http");
                responseResult(ctx, request, result);
            }
            else {
                Result result = Result.ERROR("error");
                responseResult(ctx, request, result);
            }

        }
    }

    /**
     * 服务器响应前端发过来的请求
     * @param ctx
     * @param request
     * @param result
     */
    private void responseResult(ChannelHandlerContext ctx, FullHttpRequest request, Result result) {
        boolean isKeepAlive = HttpUtil.isKeepAlive(request);
        String resultJson = result.toJson();

        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resultJson.getBytes()));
        response.headers().set(CONTENT_TYPE, "application/json;charset=utf-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Access-Control-Allow-Headers", "*");

        /* HTTP/1.1 持久化相关 */
        if (!isKeepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
