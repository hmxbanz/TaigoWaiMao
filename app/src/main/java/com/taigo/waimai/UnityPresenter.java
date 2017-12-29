package com.taigo.waimai;

import android.content.Context;

import com.taigo.waimai.common.CommonTools;
import com.taigo.waimai.common.NLog;
import com.taigo.waimai.server.HttpException;
import com.taigo.waimai.server.async.OnDataListener;
import com.taigo.waimai.server.response.BindResponse;
import com.unity3d.player.UnityPlayer;

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
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnVerifyComplete","1");
            NLog.e("response",response.getData().getExpiry_date());
        }
        else
        {
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnVerifyComplete","0");
        }

    }

    //绑定二维码
    public void bindQrcode(String qrCode,String phoneID){
        String IMEI= CommonTools.getIMEI(mActivity);
        editor.putString("CellPhoneID",IMEI).commit();
        this.qrCode=qrCode;
        this.phoneID=IMEI;
        LoadDialog.show(context);
        atm.request(BINDQRCODE,this);

    }
}