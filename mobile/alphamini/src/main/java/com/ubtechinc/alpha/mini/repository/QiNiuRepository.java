package com.ubtechinc.alpha.mini.repository;

import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.ubtech.utilcode.utils.LogUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by riley.zhang on 2018/6/13.
 */

public class QiNiuRepository {
    private final static String TAG = QiNiuRepository.class.getSimpleName();
    private final static String AK = "OJb5DHhgOxDo42se8R2JvwLyaykWLUBYowqMA3Nu";
    private final static String SK = "tRfcP40zLvGUwfVANQEClOnyn2ATb2spLki9K7cH";
    public final static String BASE_URL = "http://pak6wgbx3.bkt.clouddn.com";
    private final static String QI_NIU_BUCKET = "alphamini";
    private static QiNiuRepository mInstance;
    private UploadManager mUploadManager;
    private String mToken;
    private Auth mAuth;

    private QiNiuRepository() {
        mAuth = Auth.create(AK, SK);
        uploadFile();
    }

    public static QiNiuRepository getInstance() {
        if (mInstance == null) {
            synchronized (QiNiuRepository.class) {
                if (mInstance == null) {
                    mInstance = new QiNiuRepository();
                }
            }
        }
        return mInstance;
    }

    private void uploadFile() {
        //断点上传
        String dirPath = "/storage/emulated/0/Download";
        Recorder recorder = null;
        try{
            File f = File.createTempFile("qiniu_xxxx", ".tmp");
            LogUtils.d(TAG, f.getAbsolutePath().toString());
            dirPath = f.getParent();
            //设置记录断点的文件的路径
            recorder = new FileRecorder(dirPath);
        } catch(Exception e) {
            e.printStackTrace();
        }

        final String dirPath1 = dirPath;
        //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = new KeyGenerator(){
            public String gen(String key, File file){
                // 不必使用url_safe_base64转换，uploadManager内部会处理
                // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                String path = key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
                LogUtils.d(TAG, "path = " + path);
                File f = new File(dirPath1, UrlSafeBase64.encodeToString(path));
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));
                    String tempString = null;
                    int line = 1;
                    try {
                        while ((tempString = reader.readLine()) != null) {
                            Log.d(TAG, "line " + line + ": " + tempString);
                            line++;
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        try{
                            reader.close();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return path;
            }
        };

        Configuration config = new Configuration.Builder()
                // recorder 分片上传时，已上传片记录器
                // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .recorder(recorder, keyGen)
                .build();
        mUploadManager = new UploadManager(config);
    }

    public void uploadData(byte[] data, String key, final UploadFileListener uploadFileListener) {
        mToken = mAuth.uploadToken(QI_NIU_BUCKET, key, 3600, new StringMap().put("insertOnly", 0));
        mUploadManager.put(data, key, mToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                LogUtils.i(TAG, "uploadData key = " + key + " ResponseInfo = " + info + " response = " + response);
                if (info.isOK()) {
                    uploadFileListener.onSuccess(key, info, response);
                }else {
                    uploadFileListener.onFail();
                }
            }
        }, new UploadOptions(null, "test-type", false, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                //percent 上传的百分比
                LogUtils.i(TAG, "uploadData percent = " + percent);
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    public void uploadFilePath(String filePath, String name, final UploadFileListener uploadFileListener) {
        //new StringMap().put("insertOnly", 0) 设置上传的时候覆盖掉之前的文件
        mToken = mAuth.uploadToken(QI_NIU_BUCKET, name, 3600, new StringMap().put("insertOnly", 0));

        mUploadManager.put(filePath, name, mToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                LogUtils.i(TAG, "uploadFilePath name = " + key + " ResponseInfo = " + info + " response = " + response);
                if (info.isOK()) {
                    uploadFileListener.onSuccess(key, info, response);
                }else {
                    uploadFileListener.onFail();
                }
            }
        }, new UploadOptions(null, "test-type", true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                //percent 上传的百分比
                LogUtils.i(TAG, "uploadFilePath percent = " + percent);
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    public void uploadFile(File file, String key, final UploadFileListener uploadFileListener) {

        mToken = mAuth.uploadToken(QI_NIU_BUCKET, key, 3600, new StringMap().put("insertOnly", 0));
        mUploadManager.put(file, key, mToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if (info.isOK()) {
                    uploadFileListener.onSuccess(key, info, response);
                }else {
                    uploadFileListener.onFail();
                }
            }
        }, new UploadOptions(null, "test-type", true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                uploadFileListener.onProgress(key, percent);
            }
        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                return false;
            }
        }));
    }

    public interface UploadFileListener{
        void onProgress(String key, double percent);
        void onSuccess(String key, ResponseInfo info, JSONObject response);
        void onFail();
    }
}
