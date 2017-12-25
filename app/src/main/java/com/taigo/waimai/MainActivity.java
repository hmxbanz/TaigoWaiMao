package com.taigo.waimai;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.taigo.waimai.common.NLog;
import com.taigo.waimai.common.NToast;
import com.taigo.waimai.permissionLibrary.PermissionsManager;
import com.taigo.waimai.permissionLibrary.PermissionsResultAction;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
