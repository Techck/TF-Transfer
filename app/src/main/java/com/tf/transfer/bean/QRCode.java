package com.tf.transfer.bean;

/**
 * @author huangyue
 * @date 2018/11/09 14:58
 * @Description 二维码数据格式
 */
public class QRCode {

    private long id;
    private String username;
    private String code;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
