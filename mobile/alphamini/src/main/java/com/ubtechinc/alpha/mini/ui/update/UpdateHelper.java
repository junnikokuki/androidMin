package com.ubtechinc.alpha.mini.ui.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.ubtech.utilcode.utils.ToastUtils;
import com.ubtechinc.alpha.mini.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * @作者：liudongyang
 * @日期: 18/6/25 16:39
 * @描述: app升级相关的工具方法
 */
public class UpdateHelper {

    /**
     * 默认的密码字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
     */
    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final static String TAG = "UpdateHelper";

    public static final String FILENAME = "mini.apk";

    public static final String DOWNLOAD_ID = "download_id";

    public static boolean hasApkFile() {
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mini/" + FILENAME;
        Log.i(TAG, "checkExits: " + apkPath);
        File file = new File(apkPath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static void delete(){
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mini/" + FILENAME;
        Log.i(TAG, "checkExits: " + apkPath);
        File file = new File(apkPath);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isApkValitle(String valitleApkMD5, String name) {
        boolean flag = false;
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mini/" + name;
        String md5String = getFileMD5String(new File(apkPath));
        Log.i(TAG, "isApkValitle: remote md5 " + valitleApkMD5);
        Log.i(TAG, "isApkValitle: cacula md5 " + md5String);
        if (md5String.equals(valitleApkMD5)) {
            flag = true;
        }
        return flag;
    }


    /**
     * 生成文件的md5校验值
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static String getFileMD5String(File file) {
        InputStream fis = null;
        MessageDigest messageDigest = null;
        try {
            fis = new FileInputStream(file);
            messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int numRead = 0;
            while ((numRead = fis.read(buffer)) > 0) {
                messageDigest.update(buffer, 0, numRead);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bufferToHex(messageDigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同
        char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }


    public static boolean checkIsDownloading(Context ctx, long downloadId) {
        boolean flag = false;
        DownloadManager manager = (DownloadManager) ctx.getSystemService(DOWNLOAD_SERVICE);
        Cursor cursor = null;
        try {
            cursor = manager.query(new DownloadManager.Query().setFilterById(downloadId));
            if (cursor != null && cursor.moveToFirst()) {
                //下载状态
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_RUNNING) {
                    flag = true;
                }
            }
        } finally {
            cursor.close();
        }
        return flag;
    }

    //install with - downloadId
    public static void installMiniAppsWithDownloadId(Context context, long downloadId) {
        Log.i(TAG, "installMiniAppsWithDownloadId:  downloadId " + downloadId);
        DownloadManager manager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Uri downloadUri = manager.getUriForDownloadedFile(downloadId);
        if (downloadUri != null) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(downloadUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            ToastUtils.showShortToast(R.string.app_update_failed);
        }
    }

    public static void installMiniAppsWithLocalUri(Context context) {
        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/mini/" + FILENAME;
        Uri uri =  Uri.fromFile(new File(apkPath));
        if (uri != null) {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        } else {
            Log.i(TAG, "Local apk don't exists ");
        }
    }




}
