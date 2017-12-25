package com.taigo.waimai.server;

import android.content.Context;

import okhttp3.OkHttpClient;

/**
 * Created by hmxbanz on 2017/5/2.
 */

public class HttpAction extends OkHttpClient {
    private static HttpAction instance;

    public HttpAction()
    {

    }
    /**
     * get the SyncHttpClient instance
     *
     * @return
     */
    public static HttpAction getInstance(Context context) {
        if (instance == null) {
            synchronized (HttpAction.class) {
                if (instance == null) {
                    instance = new HttpAction();
                }
            }
        }
        //cookieStore = new PersistentCookieStore(context);
        //instance.setCookieStore(cookieStore);
        return instance;
    }

}


