package com.example.app_making;

public class Customer {
    String mobile;
    String msg;
    int resId;

    public Customer(String mobile, String msg) {
        this.mobile = mobile;
        this.msg = msg;
    }

    public Customer(String mobile, String msg, int resId) {
        this.mobile = mobile;
        this.msg = msg;
        this.resId = resId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }


}
