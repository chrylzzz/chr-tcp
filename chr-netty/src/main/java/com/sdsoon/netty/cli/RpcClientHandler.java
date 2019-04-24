package com.sdsoon.netty.cli;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

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

        //msg：拿到服务端写过来的内容，返回的
        response = msg;
        System.err.println("  验证客户端是否收到消息 ：" + response);

        ctx.write(" 确认 客户端   已经  接受了消息 ~ ");

        ctx.flush();
        ctx.close();

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
//        当 TCP 连接断开时, 会回调 channelInactive 方法,
// 因此我们在这个方法中调用 client.doConnect() 来进行重连
        new RpcClient().doConnect();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {//state有三种状态
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
        System.err.println("---READER_IDLE---");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
        System.err.println("---WRITER_IDLE---");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
        System.err.println("---ALL_IDLE---");
    }
}
