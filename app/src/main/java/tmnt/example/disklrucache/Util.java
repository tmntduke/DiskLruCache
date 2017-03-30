package tmnt.example.disklrucache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tmnt on 2017/3/29.
 */

public class Util {

    private static final String TAG = "Util";

    public static File getDiskCacheFile(Context context, String name) {
        String cache = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cache = context.getExternalCacheDir().getPath();
            Log.i(TAG, "getDiskCacheFile: "+cache);
        } else {
            cache = context.getCacheDir().getPath();
        }
        return new File(cache + File.separator + name);
    }

    public static int getVersionCode(Context context) {
        int version = 1;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String md5(String key) {
        String hashKey = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(key.getBytes());
            hashKey = hexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hashKey = String.valueOf(key.hashCode());
        }
        return hashKey;
    }

    public static String hexString(byte[] bytes) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xff & bytes[i]);
            if (hex.length() == 1) {
                buffer.append("0");
            } else {
                buffer.append(hex);
            }
        }
        return buffer.toString();
    }

}
