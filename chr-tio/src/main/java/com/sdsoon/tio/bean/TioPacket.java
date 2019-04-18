package com.sdsoon.tio.bean;

import org.tio.core.intf.Packet;

/**
 * Created By Chr on 2019/4/15.
 */
public class TioPacket extends Packet {
    public static final int HEADER_LENGHT = 4;//消息头的长度
    public static final String CHARSET = "utf-8";
    private static final long serialVersionUID = -3930523212733104829L;


    private byte[] body;

    /**
     * @return the body
     */
    public byte[] getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(byte[] body) {
        this.body = body;
    }
}