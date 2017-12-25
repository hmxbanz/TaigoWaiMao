package com.taigo.waimai.server.request;

public class UpdateRequest {

    private final String access_key;
    private String nick_name;

    public UpdateRequest(String nickName, String token) {
        this.nick_name=nickName;
        this.access_key=token;
    }

    public String getAccess_key() {
        return access_key;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }
}
