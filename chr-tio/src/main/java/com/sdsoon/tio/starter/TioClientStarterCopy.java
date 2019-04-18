package com.sdsoon.tio.starter;

import com.alibaba.fastjson.JSON;
import com.sdsoon.tio.bean.Const;
import com.sdsoon.tio.bean.TestBean;
import com.sdsoon.tio.bean.TioPacket;
import com.sdsoon.tio.cli.TioClientAioHandler;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;

import java.io.UnsupportedEncodingException;

/**
 * Created By Chr on 2019/4/15.
 */
public class TioClientStarterCopy {
    //连接服务端，服务器节点
    public static Node serverNode = new Node(Const.SERVER, Const.PORT);
    //handler, 包括编码、解码、消息处理
    public static ClientAioHandler tioClientHandler = new TioClientAioHandler();
    //事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
    public static ClientAioListener aioListener = null;
    //断链后自动连接的，不想自动连接请设为null
    private static ReconnConf reconnConf = new ReconnConf(5000L);
    //一组连接共用的上下文对象
    public static ClientGroupContext clientGroupContext = new ClientGroupContext(tioClientHandler, aioListener, reconnConf);
    //客户端入口：tioClient
    public static TioClient tioClient = null;
    //channel：建立tcp连接之后，通道channel就会产生
    public static ClientChannelContext clientChannelContext = null;

    /**
     * 启动程序入口
     */
    public static void main(String[] args) throws Exception {
        clientGroupContext.setHeartbeatTimeout(Const.TIMEOUT);
        tioClient = new TioClient(clientGroupContext);
        clientChannelContext = tioClient.connect(serverNode);
        //连上后，发条消息玩玩
        sendJaBe();
    }

    public static void sendJaBe() throws UnsupportedEncodingException {
        TioPacket requestPacket = new TioPacket();

        TestBean t = new TestBean();
        t.setXh(1);t.setDl((byte) 2);
        t.setQd(3);t.setImei("imei");
        t.setImsi("imsi");t.setTimel(System.currentTimeMillis());

        String s = JSON.toJSONString(t);


        requestPacket.setBody(s.getBytes(TioPacket.CHARSET));

        //需要在channel中发送 数据信息
        Tio.send(clientChannelContext, requestPacket);
        // Tio.send(clientGroupContext,TcpConfig.HOST,TcpConfig.PORT,requestPacket);
    }
}