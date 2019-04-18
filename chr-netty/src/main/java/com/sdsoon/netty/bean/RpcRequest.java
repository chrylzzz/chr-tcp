package com.sdsoon.netty.bean;

import java.io.Serializable;

/**
 * 发送和接口的数据包装
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcRequest implements Serializable {


    private static final long serialVersionUID = -3789942167907477118L;
    private String data;
    private Long time;
    private String imso;
    private Integer id;


    public RpcRequest() {
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getImso() {
        return imso;
    }

    public void setImso(String imso) {
        this.imso = imso;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "data='" + data + '\'' +
                ", time=" + time +
                ", imso='" + imso + '\'' +
                ", id=" + id +
                '}';
    }
}
