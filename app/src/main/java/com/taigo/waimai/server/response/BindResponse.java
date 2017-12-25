package com.taigo.waimai.server.response;


public class BindResponse {


    /**
     * data : {"expiry_date":"2018-06-21","sn_qr":"a61005c3329d3c73219f0972369ffb3d"}
     * code : 1
     * msg : Binding success.
     */

    private DataBean data;
    private int code;
    private String msg;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * expiry_date : 2018-06-21
         * sn_qr : a61005c3329d3c73219f0972369ffb3d
         */

        private String expiry_date;
        private String sn_qr;

        public String getExpiry_date() {
            return expiry_date;
        }

        public void setExpiry_date(String expiry_date) {
            this.expiry_date = expiry_date;
        }

        public String getSn_qr() {
            return sn_qr;
        }

        public void setSn_qr(String sn_qr) {
            this.sn_qr = sn_qr;
        }
    }
}
