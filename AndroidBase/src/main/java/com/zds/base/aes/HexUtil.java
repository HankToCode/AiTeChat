package com.zds.base.aes;

/**
 * @author Administrator
 * 日期 2018/3/22
 * 描述
 */

import android.util.Base64;

/**
 * 进制转化
 *
 * @author
 */
public class HexUtil {

    //base64字符串转byte[]
    public static byte[] base64String2ByteFun(String base64Str){
        return Base64.decode(base64Str,Base64.DEFAULT);
    }
    //byte[]转base64
    public static String byte2Base64StringFun(byte[] b){
        return Base64.encodeToString(b,Base64.DEFAULT);
    }

}