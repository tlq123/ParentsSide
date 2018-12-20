package com.cszj.ps.bean;

/*
短息验证码
{"status":"success","msg":"请求成功","data":{
"statusCode": "000000",
"msg":"发送成功"
}}

 */
public class AuthCodeInfo  {
    private String status ;
    private String msg ;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
