package com.taigo.waimai.common;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.TELEPHONY_SERVICE;

public class CommonTools {
    /**
     * 获取设备内容
     */
    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }
            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //请隐藏menu一直显示，不管手机有没有物理menu按键
    public static void setOverflowShowingAlways(Context context) {
        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //时间格式化
    public static String formatDateTime(long time) {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (0 == time) {
            return "";
        }
        return mDateFormat.format(new Date(time));
    }
    //时间格式化/Date(3432433434)/
    public static String formatDateTime2(String time) {
        String s=time.substring(6,time.indexOf(")"));
        long date= Long.parseLong(s);
        return formatDateTime(date);
    }
    //检测包名的应用是否已经安装在手机
    public static boolean checkBrowser(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(
                    packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    //检查是否安装后第一次运行
    public static boolean isFristRun(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserConfig", context.MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("IsFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun) {
            editor.putBoolean("IsFirstRun", false);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }
    /**
     * 判断SDCard是否存在,并可写
     *
     * @return
     */
    public static boolean checkSDCard() {
        String flag = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(flag);
    }
    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
    /**
     * 判断 一个字段的值否为空
     *
     * @param s
     * @return
     * @author Michael.Zhang 2013-9-7 下午4:39:00
     */

    public static boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }
        return false;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    /**
     * 获得当前的版本信息
     *
     * @return
     */
    public static String[] getVersionInfo(Context context) {
        String[] version = new String[2];

        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            version[0] = String.valueOf(packageInfo.versionCode);
            version[1] = packageInfo.versionName;
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
    public static String getDateStr() throws ParseException {
        Calendar now = Calendar.getInstance();
//        System.out.println("年: " + now.get(Calendar.YEAR));
//        System.out.println("月: " + (now.get(Calendar.MONTH) + 1) + "");
//        System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));
//        System.out.println("时: " + now.get(Calendar.HOUR_OF_DAY));
//        System.out.println("分: " + now.get(Calendar.MINUTE));
//        System.out.println("秒: " + now.get(Calendar.SECOND));
//        System.out.println("当前时间毫秒数：" + now.getTimeInMillis());
//        System.out.println(now.getTime());
//
        Date d = new Date();
        System.out.println(d);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(d);

        String str = "2012-1-13 17:26:33";  //要跟上面sdf定义的格式一样
        Date today = sdf.parse(str);
        System.out.println("字符串转成日期：" + today);
        return dateNowStr;
    }

    //获取手机IMEI
    public static String getIMEI(Context cx) {
        if (ActivityCompat.checkSelfPermission(cx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "";
        }

        TelephonyManager telephonyManager = (TelephonyManager) cx.getSystemService(TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        NLog.setDebug(true);
        NLog.e("imei", imei);
        return imei;
    }

}
