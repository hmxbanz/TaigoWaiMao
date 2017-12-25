package com.taigo.waimai.server.request;

public class NewPasswordRequest {

    private String userName;
    private String password;
    private String captcha;

    public NewPasswordRequest(String userName, String password, String captcha) {
        this.userName = userName;
        this.password = password;
        this.captcha = captcha;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCaptcha() {
        return captcha;
    }
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
