package com.taigo.waimai;

import android.content.Context;

import com.taigo.waimai.common.NLog;
import com.taigo.waimai.server.HttpException;
import com.taigo.waimai.server.async.OnDataListener;
import com.taigo.waimai.server.response.BindResponse;

/**
 * Created by hmxbanz on 2017/4/5.
 */

public class UnityPresenter extends BasePresenter implements OnDataListener {
    private static final int BINDQRCODE = 1;
    private UnityPlayerActivity mActivity;
    private String qrCode,phoneID;

    public UnityPresenter(Context context){
        super(context);
        mActivity = (UnityPlayerActivity) context;
    }

    public void init() {
        //LoadDialog.show(context);
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return mUserAction.bindQRCode(this.qrCode, this.phoneID);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        LoadDialog.dismiss(context);
        if (result == null) {
            return;
        }

        BindResponse response= (BindResponse)result;
        if (response.getCode() == 1) {
            NLog.e("response",response.getData().getExpiry_date());
        }
    }

    //绑定二维码
    public void bindQrcode(String qrCode,String phoneID){
        this.qrCode=qrCode;
        this.phoneID="aaaaaaaaaa";
        LoadDialog.show(context);
        atm.request(BINDQRCODE,this);

    }
}