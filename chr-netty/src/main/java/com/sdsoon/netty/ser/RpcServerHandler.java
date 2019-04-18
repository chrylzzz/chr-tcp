package com.sdsoon.netty.ser;


import com.sdsoon.netty.bean.RpcRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created By Chr on 2019/4/15.
 */
@ChannelHandler.Sharable
public class RpcServerHandler extends ChannelInboundHandlerAdapter {

    public RpcServerHandler() {
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        RpcRequest rpcRequest = (RpcRequest) msg;

        System.err.println(rpcRequest);


        ctx.write(" 服务端   已经  收到了消息 ~ ");
        ctx.flush();
        ctx.close();
    }

}
