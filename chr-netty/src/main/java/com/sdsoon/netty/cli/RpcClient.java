package com.sdsoon.netty.cli;


import com.sdsoon.netty.bean.RpcRequest;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;

/**
 * Created By Chr on 2019/4/15.
 */
public class RpcClient {

    public static void main(String args[]) throws IOException {
        Object o = new RpcClient().create();
        //System.err.println(" 客户端验证：" + o.toString());
//        System.in.read();
    }

    //    private final static String serviceAddress = "192.168.0.148:60000";
    private final static String serviceAddress = "127.0.0.1:700";

    public Object create() {


        //写回服务端数据
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setId(1);
        rpcRequest.setData("客户端发送消息的数据对象~~~");
        rpcRequest.setImso("imso");
        rpcRequest.setTime(System.currentTimeMillis());

        //解析host和ip
        String[] arrs = serviceAddress.split(":");

        String host = arrs[0];
        int port = Integer.parseInt(arrs[1]);
        //Socket  Netty 进行连接  Socket(ip,port)---->Netty

        final RpcClientHandler rpcClientHandler = new RpcClientHandler();

        //通过netty方式进行连接和发送
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            // 是否启用心跳保活机机制
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            pipeline.addLast("encoder", new ObjectEncoder());
                            pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));


//                            pipeline.addLast(new StringEncoder());//对 String 对象自动编码,属于出站站处理器
//                            pipeline.addLast(new StringDecoder());//把网络字节流自动解码为 String 对象，属于入站处理器


                            pipeline.addLast(rpcClientHandler);

                        }
                    });

            //连接服务 地址
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {

                System.err.println(" Netty服务端连接成功，等待发送数据: ==================");

            }
            //将封装好的rpcRequest 对象写过去-》服务端数据的返回
            //写回到服务端
            future.channel().writeAndFlush(rpcRequest);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            //e.printStackTrace();
            System.err.println(" Netty服务端连接失败:============= ");

        }
//        } finally {
//            //关流，客户端不回消息了
//            group.shutdownGracefully();
//        }

        return rpcClientHandler.getResponse();

    }
}
