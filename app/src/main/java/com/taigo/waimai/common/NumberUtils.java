package com.taigo.waimai.common;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by PVer on 2017/6/26.
 */

public class NumberUtils {
    // byte转十六进制字符串
    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase(Locale.CHINA);
        }
        return ret;
    }

    /** * 将16进制的字符串转换为字节数组 *
     * @param message * @return 字节数组 */
    public static byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();
        String[] hexStr = new String[len];
        byte[] bytes = new byte[len];
        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }

    public static byte[] reverseBytes(byte[] a)
    {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }
        return b;
    }

    /**
     * 将16进制 转换成10进制
     *
     * @param str
     * @return
     */
    public static String print10(String str) {

        StringBuffer buff = new StringBuffer();
        String array[] = str.split(" ");
        for (int i = 0; i < array.length; i++) {
            int num = Integer.parseInt(array[i], 16);
            buff.append(String.valueOf((char) num));
        }
        return buff.toString();
    }

    /**
     * 将字符串转byte[]
     *
     * @param str
     * @return
     */
    public static byte[] Str2byte(String str) {

        byte[] srtbyte = null;
        try {
            srtbyte = str.getBytes("UTF-8");
            String res = new String(srtbyte,"UTF-8");
            System.out.println(res);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return srtbyte;
    }

}
