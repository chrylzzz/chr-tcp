package com.sdsoon.netty.ser;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.io.IOException;

/**
 * 基于Netty：
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class RpcServer {

    public static void main(String args[]) throws IOException {
        new RpcServer().publisher();

//        System.in.read();
    }

    private final static String serviceAddress = "127.0.0.1:700";

    public void publisher() {

        final RpcServerHandler rpcServerHandler = new RpcServerHandler();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //启动一个监听 Netty ServerSocket(ip,port)     Socket 监听端口，io交互
        try {


            //启动netty服务
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();


                    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                    pipeline.addLast("encoder", new ObjectEncoder());
                    //禁用缓存
                    pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));


//                    pipeline.addLast(new StringEncoder());//对 String 对象自动编码,属于出站站处理器
//                    pipeline.addLast(new StringDecoder());//把网络字节流自动解码为 String 对象，属于入站处理器

                    pipeline.addLast(rpcServerHandler);


                }
            }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

            //通过netty  进行监听  8080

            String[] address = serviceAddress.split(":");
            String ip = address[0];
            int port = Integer.parseInt(address[1]);

            //监听的url
            ChannelFuture future = bootstrap.bind(ip, port).sync();


            System.err.println(" Netty服务端启动成功，等待客户端的连接: ");
            future.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
