package com.yuy.runCodeOnDocker.docker;

import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.yuy.entity.Result;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class RunCodeResultCallback extends ResultCallbackTemplate<ExecStartResultCallback, Frame> {

    // controller返回的result， 为了在docker回调中把结果写入result
    private Result result;

    // WebSocket ctx
    private ChannelHandlerContext ctx;

    public RunCodeResultCallback(Result result, ChannelHandlerContext ctx) {
        this.result = result;
        this.ctx = ctx;
    }

    public void onNext(Frame frame) {
        if (frame != null) {
            String msg = new String(frame.getPayload());
            switch (frame.getStreamType()) {
                case STDOUT:
                case RAW:
                case STDERR:
                    if(result != null) {
                        System.out.println(msg);
                        result.data("result", msg);
                    }
                    if(ctx != null) {
                        System.out.println(msg);
                        ctx.writeAndFlush(new TextWebSocketFrame("代码执行结果: \n" + msg));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
