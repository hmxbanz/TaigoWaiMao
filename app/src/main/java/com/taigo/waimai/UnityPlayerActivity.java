package com.taigo.waimai;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Toast;

import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.taigo.waimai.common.NLog;
import com.taigo.waimai.common.NToast;
import com.taigo.waimai.permissionLibrary.PermissionsManager;
import com.taigo.waimai.permissionLibrary.PermissionsResultAction;
import com.taigo.waimai.service.BluetoothService;
import com.unity3d.player.UnityPlayer;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.List;

public class UnityPlayerActivity extends BaseActivity
{
    private static final int QRSCANNING = 1;
    protected UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code
    private UnityPresenter presenter;
    private String qrCode,IMEI;

    private static final String TAG = UnityPlayerActivity.class.getSimpleName();
    private BluetoothService mBluetoothService;
    private BluetoothGattCharacteristic writecharacteristic;
    private boolean isConnect;
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);
            //mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    private BluetoothService.Callback callback = new BluetoothService.Callback() {

        @Override
        public void onStartScan() {

        }

        @Override
        public void onScanning(ScanResult result) {
            if(result.getDevice().getName() !=null && (result.getDevice().getName().contains("Taigo") || result.getDevice().getName().contains("HW-TGG"))) {
                UnityPlayer.UnitySendMessage("ConnectMgr", "OnScanning",result.getDevice().getAddress());
            }
        }

        @Override
        public void onScanComplete() {
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnScanComplete","");
        }

        @Override
        public void onConnecting() {
        }

        @Override
        public void onConnectFail() {
            NToast.shortToast(UnityPlayerActivity.this, "Connect error.");
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnConnectComplete","0");
        }

        @Override
        public void onDisConnected() {
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnDisConnected","");
            NToast.shortToast(UnityPlayerActivity.this, "DisConnected");
        }

        @Override
        public void onServicesDiscovered() {
            // 连接成功
            isConnect=true;
            NToast.shortToast(UnityPlayerActivity.this, " Successful Connected!");
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnConnectComplete","1");

            showData();
        }
    };

    // Setup activity layout
    @Override protected void onCreate (Bundle savedInstanceState)
    {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy

        mUnityPlayer = new UnityPlayer(this);
        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();

        //init();

        presenter = new UnityPresenter(this);
        bindService();
        //isActivitied();
    }

    public void init() {
        String[] Permissions=new String[]{Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        //权限申请
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this,
                Permissions,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {
                        Toast.makeText(UnityPlayerActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }, true);
    }
    public void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        bindIntent.putExtra("HomeFragment","从HomeFragment");
        bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);

    }

    public void unbindService() {
        if(mFhrSCon!=null) {
            unbindService(mFhrSCon);
            mFhrSCon=null;
        }
    }

    // 找枪
    public void ScanDevice(){
        if ( mBluetoothService != null)
            mBluetoothService.scanDevice();
        else
            bindService();
    }

    // 连接枪
    public void ScanAndConnect(String mac){
        if ( mBluetoothService != null)
            mBluetoothService.scanAndConnect5(mac);
    }

    // 获取蓝牙是否开启
    public  boolean GetBluetoothState(){
        BluetoothAdapter blueadapter=BluetoothAdapter.getDefaultAdapter();
        if (blueadapter == null){
            return  false;
        } else {
            return  blueadapter.isEnabled();
        }
    }
    //数据传输
    public void showData() {
        BluetoothGatt gatt = mBluetoothService.getGatt();
        for (BluetoothGattService servicess : gatt.getServices()) {
            Log.w(TAG, "================== find service: " + servicess.getUuid().toString());
            if (servicess.getUuid().toString().startsWith("0000ae00")) {//枪发给手机
                mBluetoothService.setService(servicess);
                List<BluetoothGattCharacteristic> characteristics = servicess.getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {

                    if (characteristic.getUuid().toString().startsWith("0000ae01"))
                        writecharacteristic = characteristic;

                    Log.w(TAG, "================== find characteristics count: " + characteristics.size());
//                            BluetoothGattCharacteristic characteristic = characteristics.get(0);
                    Log.w(TAG, "================== find characteristic: " + characteristic.getUuid().toString());
                    //Log.w(TAG, "================== characteristic value: " + byte2HexStr(characteristic.getValue()));
                    //gatt.setCharacteristicNotification(characteristic, true);
                    Log.w(TAG, "================== Thread : " + Thread.currentThread().getId());
                    if (characteristic.getUuid().toString().startsWith("0000ae02")) {
                        mBluetoothService.setCharacteristic(characteristic);

                    }
                }
            }
        }

        final BluetoothGattCharacteristic characteristic = mBluetoothService.getCharacteristic();
        mBluetoothService.notify(characteristic.getService().getUuid().toString(), characteristic.getUuid().toString(), new BleCharacterCallback() {

            @Override
            public void onSuccess(final BluetoothGattCharacteristic characteristic) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String s = String.valueOf(HexUtil.encodeHex(characteristic.getValue()));
                        //String s10=NumberUtils.print10(s);
                        //NLog.d("BLEBLE",s+"-----");
                        //UnityPlayer.UnitySendMessage("Main Camera","eee",s10);

                        // For all other profiles, writes the data formatted in HEX.对于所有的文件，写入十六进制格式的文件
                        //这里读取到数据
                        final byte[] data = characteristic.getValue();
                        for (int i = 0; i < data.length; i++) {
                            //System.out.println("BLEBLE---原始byte" + (int)data[i]);
                        }
                        if (data != null && data.length > 0) {
                            final StringBuilder stringBuilder = new StringBuilder(data.length);
                            for (byte byteChar : data)
                                //以十六进制的形式输出
                                stringBuilder.append(String.format("%02x ", byteChar));
                            // intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
                            //intent.putExtra(EXTRA_DATA, new String(data));
                            NLog.d("BLEBLE---转换成16进制", stringBuilder.toString());
                        }
                        //Integer.toHexString(10)
                        byte command = data[6];
//                                int aaaaa=command & 0x01;
//                                NLog.d("BLEBLE",aaaaa);


                        NLog.d("BLEBLE---未知", s);
                        String x = s.substring(14, 18);
                        String y = s.substring(18, 22);

                        //NLog.d("BLEBLE>>>>",x+"-"+y);

                        int xx=Integer.parseInt(x,16);
                        int yy=Integer.parseInt(y,16);

                        //NLog.d("xxxx+yyyy:",x+"-"+y);

                        //NLog.d("xxxxyyyy:",xx+"-"+yy);

                        if((xx>400 && xx<600) && (yy>400 && yy<600))
                            UnityPlayer.UnitySendMessage("Main Camera","eee","op");
                        else
                        {
                            if(xx !=0 && yy !=0)
                                UnityPlayer.UnitySendMessage("Main Camera","eee",xx+","+yy);

                        }

                        if ((int) command == 0) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "j");
                            // txtShow.setText("松开");
                        }

                        if ((int) command == 1) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "a");
                            //txtShow.setText("射击");
                        }

                        if ((int) command == 2) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "b");
                            // txtShow.setText("上弹");
                        } else if ((int) command == 3) {

                        } else if ((int) command == 4) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "e");
                            //txtShow.setText("换枪");
                        } else if ((int) command == 8) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "c");
                            //txtShow.setText("补血");
                        } else if ((int) command == 10) {
                            UnityPlayer.UnitySendMessage("Main Camera", "eee", "h");
                            //txtShow.setText("换枪");
                        } else if ((int) command == 20) {
                            //txtShow.setText("无用");
                        }

                    }
                });
            }

            @Override
            public void onFailure(final BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onInitiatedResult(boolean result) {

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * 处理二维码扫描结果
         */
        switch (requestCode) {
            case QRSCANNING:
                //处理扫描结果（在界面上显示）
                if (null != data) {
                    Bundle bundle = data.getExtras();
                    if (bundle == null) {
                        return;
                    }
                    if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                        qrCode = bundle.getString(CodeUtils.RESULT_STRING);
                        presenter.bindQrcode(qrCode,IMEI);
                        Toast.makeText(this, "result:" + qrCode, Toast.LENGTH_LONG).show();
                    } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                        Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }


    @Override protected void onNewIntent(Intent intent)
    {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
    }

    // Quit Unity
    @Override protected void onDestroy ()
    {
        mUnityPlayer.quit();
        super.onDestroy();
    }

    // Pause Unity
    @Override protected void onPause()
    {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override protected void onResume()
    {
        super.onResume();
        mUnityPlayer.resume();
    }

    // Low Memory Unity
    @Override public void onLowMemory()
    {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override public void onTrimMemory(int level)
    {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL)
        {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
            return mUnityPlayer.injectEvent(event);
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override public boolean onKeyUp(int keyCode, KeyEvent event)     { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event)   { return mUnityPlayer.injectEvent(event); }
    @Override public boolean onTouchEvent(MotionEvent event)          { return mUnityPlayer.injectEvent(event); }
    /*API12*/ public boolean onGenericMotionEvent(MotionEvent event)  { return mUnityPlayer.injectEvent(event); }

    //接口
    private void isActivitied()
    {
        if("".equals(presenter.sp.getString("CellPhoneID","")))
        {
            startActivityForResult(new Intent(this,QrCodeActivity.class),QRSCANNING);
        }
        else
        {
            UnityPlayer.UnitySendMessage("ConnectMgr", "OnVerifyComplete","1");
        }
    }
}
