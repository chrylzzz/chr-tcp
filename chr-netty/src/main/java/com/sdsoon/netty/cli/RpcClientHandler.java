package com.sdsoon.netty.cli;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created By Chr on 2019/4/15.
 */
//@Component
@ChannelHandler.Sharable
public class RpcClientHandler extends ChannelInboundHandlerAdapter {

//    @Scheduled(fixedRate = 5000)
//    public String show() {
//        return UUID.randomUUID().toString();
//    }


    public RpcClientHandler() {
    }

    private Object response;

    public Object getResponse() {
        return response;
    }

    /**
     * @param ctx 发送
     * @param msg 接收
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //msg：拿到服务端写过来的内容
        response = msg;
        System.err.println("  验证客户端是否收到消息 ：" + response);

        ctx.write(" 确认 客户端   已经  接受了消息 ~ ");

        ctx.flush();
        ctx.close();

    }
}
