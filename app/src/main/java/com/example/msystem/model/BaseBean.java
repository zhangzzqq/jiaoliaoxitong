package com.example.msystem.model;

import java.io.Serializable;


/**
 * Created by Administrator on 2016/5/30 0030.


import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/5/30 0030.
 */
public class BaseBean<T> implements Serializable {

    private String status;
    private String action;
    private String key;
    private String totalnum;
    private String msg;
    private T list;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTotalnum() {
        return totalnum;
    }

    public void setTotalnum(String totalnum) {
        this.totalnum = totalnum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getList() {
        return list;
    }

    public void setList(T list) {
        this.list = list;
    }

}
