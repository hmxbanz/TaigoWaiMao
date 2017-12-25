package com.taigo.waimai;

import android.content.Context;
import android.content.SharedPreferences;

import com.taigo.waimai.common.CommonTools;
import com.taigo.waimai.common.NToast;
import com.taigo.waimai.server.HttpException;
import com.taigo.waimai.server.UserAction;
import com.taigo.waimai.server.async.AsyncTaskManager;
import com.taigo.waimai.server.async.OnDataListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by PVer on 2017/5/23.
 */

public class BasePresenter implements OnDataListener {
    private static BasePresenter instance;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor editor;
    protected Context context;
    public UserAction mUserAction;
    public AsyncTaskManager atm ;

    public BasePresenter(Context context)
    {
        this.context =context;
        atm= AsyncTaskManager.getInstance(context);
        mUserAction = UserAction.getInstance(context);
        if(context != null){
            sp = this.context.getSharedPreferences("UserConfig", MODE_PRIVATE);
            editor = sp.edit();
            initData();
        }
    }
    public static BasePresenter getInstance(Context context) {
        if (instance == null) {
            synchronized (BasePresenter.class) {
                if (instance == null) {
                    instance = new BasePresenter(context);
                }
            }
        }
        return instance;
    }
    protected void initData()
    {
        mUserAction.token = sp.getString(Const.ACCESS_TOKEN,"");

    }
    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        LoadDialog.dismiss(context);
        if (!CommonTools.isNetworkConnected(context)) {
            NToast.shortToast(context, "网络不可用");
            return;
        }

    }


}