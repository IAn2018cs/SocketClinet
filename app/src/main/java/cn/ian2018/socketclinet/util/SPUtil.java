package cn.ian2018.socketclinet.util;

import android.content.Context;

/**
 * Created by chenshuai on 2020/8/7
 */
public class SPUtil {
    private static final String SP_NAME = "e2eesp";

    public static String userList = "";

    public static void saveId(Context context, String id) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString("user_id", id).apply();
    }

    public static String getId(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("user_id", "");
    }

    public static void savePublicKey(Context context, String key) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString("public_key", key).apply();
    }

    public static String getPublicKey(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("public_key", "");
    }

    public static void savePrivateKey(Context context, String key) {
        context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit().putString("private_key", key).apply();
    }

    public static String getPrivateKey(Context context) {
        return context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).getString("private_key", "");
    }
}
