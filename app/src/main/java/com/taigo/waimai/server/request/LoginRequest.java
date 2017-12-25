package com.taigo.waimai.server.request;

public class LoginRequest {

    private String phone_no;
    private String pwd;

    public LoginRequest(String userName, String password) {
        this.phone_no = userName;
        this.pwd = password;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(String phone_no) {
        this.phone_no = phone_no;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
