package cn.ian2018.socketclinet.util;


import android.util.Base64;
import android.util.Pair;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.util.Base64.DEFAULT;

public class KeyUtil {

    /**
     * 生成 AES 密钥
     *
     * @return
     */
    public static String generateAESKey() {
        try {
            // 创建随机数生成器
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            // 设置 密钥key的字节数组 作为安全随机数生成器的种子
            secureRandom.setSeed("keySeed".getBytes());

            // 创建 AES算法生成器
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            // 初始化算法生成器
            generator.init(128);
            SecretKey secretKey = generator.generateKey();
            return Base64.encodeToString(secretKey.getEncoded(), DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static SecretKey getAESKey(String key) {
        byte[] decodedKey = Base64.decode(key, DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * AES 加密
     *
     * @param key
     * @param plainData
     * @return
     */
    public static String aesEncrypt(String key, byte[] plainData) {
        try {
            // 生成密钥对象
            SecretKey secretKey = getAESKey(key);

            // 获取 AES 密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化密码器（加密模型）
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 加密数据, 返回密文
            byte[] cipherBytes = cipher.doFinal(plainData);

            return Base64.encodeToString(cipherBytes, DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * AES 解密
     *
     * @param key
     * @param encryptData
     * @return
     */
    public static byte[] aesDecrypt(String key, String encryptData) {
        try {
            // 生成密钥对象
            SecretKey secretKey = getAESKey(key);

            // 获取 AES 密码器
            Cipher cipher = Cipher.getInstance("AES");
            // 初始化密码器（加密模型）
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // 解密数据, 返回明文
            return cipher.doFinal(Base64.decode(encryptData, DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * RSA 解密
     *
     * @param privateKey
     * @param encryptData
     * @return
     */
    public static byte[] decryptData(String privateKey, String encryptData) {
        try {
            Cipher cipher2 = Cipher.getInstance("RSA");
            cipher2.init(Cipher.DECRYPT_MODE, readPrivateKey(privateKey));
            return cipher2.doFinal(Base64.decode(encryptData, DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * RSA 加密
     *
     * @param publicKey
     * @param plainData
     * @return
     */
    public static String encryptData(String publicKey, byte[] plainData) {
        try {
            // 公钥加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, readPublicKey(publicKey));
            byte[] bytes = cipher.doFinal(plainData);
            return Base64.encodeToString(bytes, DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 生成RSA密钥对
     *
     * @return
     */
    public static Pair<String, String> generateKey() {
        try {
            // 生成RSA密钥对
            KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
            gen.initialize(512);
            KeyPair keyPair = gen.generateKeyPair();

            // 获取 公钥 和 私钥
            PublicKey pubKey = keyPair.getPublic();
            PrivateKey priKey = keyPair.getPrivate();

            byte[] pubEncBytes = pubKey.getEncoded();
            byte[] priEncBytes = priKey.getEncoded();

            // 把 公钥和私钥 的 编码格式 转换为 Base64文本 方便保存
            String pubEncBase64 = Base64.encodeToString(pubEncBytes, DEFAULT);
            String priEncBase64 = Base64.encodeToString(priEncBytes, DEFAULT);
            return new Pair<>(pubEncBase64, priEncBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Pair<>("", "");
    }

    public static String signature(String privateKey, byte[] plainData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // 签名 私钥加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, readPrivateKey(privateKey));
        byte[] bytes = cipher.doFinal(plainData);
        return Base64.encodeToString(bytes, DEFAULT);
    }

    public static byte[] deSignature(String publicKey, String plainData) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // 签名 公钥解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, readPublicKey(publicKey));
        return cipher.doFinal(Base64.decode(plainData, DEFAULT));
    }

    /**
     * 将 字符串 转成 RSA 公钥
     *
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PublicKey readPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 把 公钥的Base64文本 转换为已编码的 公钥bytes
        byte[] encPubKey = Base64.decode(key, DEFAULT);

        // 创建 已编码的公钥规格
        X509EncodedKeySpec encPubKeySpec = new X509EncodedKeySpec(encPubKey);

        // 获取指定算法的密钥工厂, 根据 已编码的公钥规格, 生成公钥对象
        return KeyFactory.getInstance("RSA").generatePublic(encPubKeySpec);
    }

    /**
     * 将 字符串 转成 RSA 私钥
     *
     * @param key
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey readPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 把 私钥的Base64文本 转换为已编码的 私钥bytes
        byte[] encPriKey = Base64.decode(key, DEFAULT);

        // 创建 已编码的私钥规格
        PKCS8EncodedKeySpec encPriKeySpec = new PKCS8EncodedKeySpec(encPriKey);

        // 获取指定算法的密钥工厂, 根据 已编码的私钥规格, 生成私钥对象
        return KeyFactory.getInstance("RSA").generatePrivate(encPriKeySpec);
    }
}
