package com.sdsoon.tio.ser;

import com.alibaba.fastjson.JSON;
import com.sdsoon.tio.bean.TestBean;
import com.sdsoon.tio.bean.TioPacket;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * Created By Chr on 2019/4/15.
 */
public class TioServerAioHandler implements ServerAioHandler {
    /**
     * 解码：把接收到的ByteBuffer，解码成应用可以识别的业务消息包
     * 总的消息结构：消息头 + 消息体
     * 消息头结构：    4个字节，存储消息体的长度
     * 消息体结构：   对象的json串的byte[]
     * <p>
     * * <p>
     * 对于半包：
     * 业务端需要在AioHandler.decode()里返回一个null对象给框架，
     * 框架拿到null后，就会认为这是个半包，进而把收到的数据暂存到DecodeRunnable.lastByteBuffer，
     * 当后面再收到数据时，把DecodeRunnable.lastByteBuffer和新收到的数据组成一个新的bytebuffer给业务端，
     * 如此循环，直到业务端能组成一个packet对象给框架层。
     * <p>
     * 对于粘包：
     * 业务端在AioHandler.decode()方法中，解码一个packet对象返回给框架后，
     * 框架会自行判断是否有多余的byte没有被处理，
     * 如果有，则拿剩下的byte(bytebuffer)让业务端继续解码，
     * 直到业务端返回null或是返回packet但没有剩余byte为止。
     * <p>
     * 框架层已经做好半包和粘包的工作，业务层只需要按着业务协议解码即可，
     * 框架会处理好剩下的byte或是上次没处理完的byte的。
     */
    @Override
    public TioPacket decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
        //提醒：buffer的开始位置并不一定是0，应用需要从buffer.position()开始读取数据
        //收到的数据组不了业务包，则返回null以告诉框架数据不够
        if (readableLength < TioPacket.HEADER_LENGHT) {
            return null;
        }
        //读取消息体的长度
        int bodyLength = buffer.getInt();
        //数据不正确，则抛出AioDecodeException异常
        if (bodyLength < 0) {
            throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
        }
        //计算本次需要的数据长度
        int neededLength = TioPacket.HEADER_LENGHT + bodyLength;
        //收到的数据是否足够组包
        int isDataEnough = readableLength - neededLength;
        // 不够消息体长度(剩下的buffe组不了消息体)
        if (isDataEnough < 0) {
            return null;
        } else //组包成功
        {
            TioPacket imPacket = new TioPacket();
            if (bodyLength > 0) {
                byte[] dst = new byte[bodyLength];
                buffer.get(dst);
                imPacket.setBody(dst);
            }
            return imPacket;
        }
    }

    /**
     * 编码：把业务消息包编码为可以发送的ByteBuffer
     * 总的消息结构：消息头 + 消息体
     * 消息头结构：    4个字节，存储消息体的长度
     * 消息体结构：   对象的json串的byte[]
     */
    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        TioPacket TioPacket = (TioPacket) packet;
        byte[] body = TioPacket.getBody();
        int bodyLen = 0;
        if (body != null) {
            bodyLen = body.length;
        }
        //bytebuffer的总长度是 = 消息头的长度 + 消息体的长度
        int allLen = TioPacket.HEADER_LENGHT + bodyLen;
        //创建一个新的bytebuffer
        ByteBuffer buffer = ByteBuffer.allocate(allLen);
        //设置字节序
        buffer.order(groupContext.getByteOrder());
        //写入消息头----消息头的内容就是消息体的长度
        buffer.putInt(bodyLen);
        //写入消息体
        if (body != null) {
            buffer.put(body);
        }
        return buffer;
    }

    /**
     * 处理消息：用于接收client发来的数据
     */
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        //client发来的数据
        TioPacket TioPacket = (TioPacket) packet;
        byte[] body = TioPacket.getBody();
        if (body != null) {
            String str = new String(body, TioPacket.CHARSET);
            System.err.println(" 服务端接收的数据是" + str);

            try {
                TestBean testBean = JSON.parseObject(str, TestBean.class);
                System.err.println(testBean.getTimel());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                TioPacket resppacket = new TioPacket();
                resppacket.setBody(("收到了你的消息，你的消息是:" + str).getBytes(TioPacket.CHARSET));
                //服务端收到消息后 给client回执
                Tio.send(channelContext, resppacket);
            }

        }
        return;
    }
}