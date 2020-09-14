package com.yuy.file;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.*;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class FileUploadHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String DEFAULT_FILE_PATH = System.getProperty("user.dir") + "/src";

    private HttpPostRequestDecoder decoder;


    static {
        DiskFileUpload.baseDirectory = null;
        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) throws Exception {
        // 解析请求是否正确
        if (!request.decoderResult().isSuccess()) {
            sendError(channelHandlerContext, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        // 请求方式是否为post
        if (request.method() != HttpMethod.POST) {
            sendError(channelHandlerContext, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        String uri = request.uri();
        String fileName = null;
        if (!uri.startsWith("/src/upload")) {
            sendError(channelHandlerContext, HttpResponseStatus.NOT_FOUND);
            return;
        }

        // 如果未带文件名参数，则随机赋予
        if (request.headers().get("fileName") != null && !request.headers().get("fileName").equals("")) {
            fileName = request.headers().get("fileName");
        }
        Map<String, Object> params = getRequestParams(request);
        if (params.get("fileName") != null && !params.get("fileName").equals("")) {
            fileName = (String) params.get("fileName");
        }

        if (decoder != null) {

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


    /**
     * 解析FullHttpRequest的请求体内容，把它转换为map
     * @param request
     * @return
     */
    private static Map<String, Object> getRequestParams(FullHttpRequest request) {
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
        List<InterfaceHttpData> requestBody = decoder.getBodyHttpDatas();
        Map<String, Object> param = new HashMap<String, Object>();

        for (InterfaceHttpData data : requestBody) {
            if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                MemoryAttribute attribute = (MemoryAttribute) data;
                param.put(attribute.getName(), attribute.getValue());
            }
        }

        return param;
    }
}
