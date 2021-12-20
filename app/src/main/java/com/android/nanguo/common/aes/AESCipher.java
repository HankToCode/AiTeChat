package com.android.nanguo.common.aes;


import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Administrator
 * 日期 2018/3/21
 * 描述
 */

public class AESCipher {
    /**
     * 算法/模式/填充
     **/
    private static final String CIPHERMODE = "AES/ECB/PKCS5Padding";
    private static final String IV = "8ebf1bf4349212ba";
    private static final String PASSWORD = "2226a23e1390894b5qwe";
    private static final String DDIV = "zcwtwdnzchqweabc";
    private static final String DDPASSWORD = "qzhbzcwtlzddbzlwzxdn";

    private static SecretKeySpec createKey(String key) {
        byte[] data = null;
        if (key == null) {
            key = "";
        }
        StringBuffer sb = new StringBuffer(16);
        sb.append(key);
        while (sb.length() < 16) {
            sb.append("0");
        }
        if (sb.length() > 16) {
            sb.setLength(16);
        }

        try {
            data = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new SecretKeySpec(data, "AES");
    }

    /**
     * 加密字节数据
     **/
    public static byte[] encrypt(byte[] content, String password) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CIPHERMODE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密(结果为16进制字符串)
     **/
    public static String encrypt(String content) {
        byte[] data = null;
        try {
            data = content.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        data = encrypt(data, DDPASSWORD);
        /*if (!TextUtils.isEmpty(DemoApplication.getInstance().aesStatus)) {
            if (DemoApplication.getInstance().aesStatus.equals("old")) {
                data = encrypt(data, PASSWORD);
            } else {
                data = encrypt(data, DDPASSWORD);
                Log.d("###密匙状态加密=", DemoApplication.getInstance().aesStatus+"###密匙="+DDPASSWORD);
            }
        } else {
            data = encrypt(data, PASSWORD);
        }*/
        String result = HexUtil.byte2Base64StringFun(data);
        return result;
    }

    /**
     * 解密
     **/
    public static String decrypt(String content) {
        /*String result = "";
        if (!TextUtils.isEmpty(DemoApplication.getInstance().aesStatus)) {
            if (DemoApplication.getInstance().aesStatus.equals("old")) {
                result = decrypt(content, PASSWORD, IV);
            } else {
                result = decrypt(content, DDPASSWORD, IV);
            }
        } else {
            result = decrypt(content, PASSWORD, IV);
        }*/

        return decrypt(content, DDPASSWORD, IV);
    }

    /**
     * 解密字节数组
     **/
    public static byte[] decrypt(byte[] content, String password, String iv) {
        try {
            SecretKeySpec key = createKey(password);
            Cipher cipher = Cipher.getInstance(CIPHERMODE);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(content);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解密
     **/
    public static String decrypt(String content, String password, String iv) {
        byte[] data = null;
        try {
            if (content != null && content.contains("\n")) {
                content = content.replace("\n", "");
            }
            data = HexUtil.base64String2ByteFun(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        data = decrypt(data, password, iv);
        if (data == null)
            return null;
        String result = null;
        try {
            result = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


}
