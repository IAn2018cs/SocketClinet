package cn.ian2018.socketclinet.util;

import java.security.MessageDigest;

public class HashUtil {
    /**
     * hash类型
     */
    public static final String HASH_TYPE_MD5 = "MD5";
    public static final String HASH_TYPE_SHA1 = "SHA-1";
    public static final String HASH_TYPE_SHA256 = "SHA-256";
    public static final String HASH_TYPE_SHA384 = "SHA-384";
    public static final String HASH_TYPE_SHA512 = "SHA-512";

    /**
     * 计算字符串的SHA-1值
     *
     * @param value
     * @return
     */
    public static String hexSHA1(String value) {
        return hexSHA(value, HASH_TYPE_SHA1);
    }

    /**
     * 计算字符串的SHA值，可指定hash算法
     *
     * @param value
     * @param hashType HASH_TYPE_
     * @return
     */
    public static String hexSHA(String value, String hashType) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashType);
            md.update(value.getBytes("utf-8"));
            byte[] digest = md.digest();
            return byteToHexString(digest);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 计算Hex值
     *
     * @param bytes
     * @return
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : bytes) {
            sb.append(Integer.toHexString(aB & 0xFF));
        }
        return sb.toString();
    }

    public static byte[] hexStringToByte(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }
}
