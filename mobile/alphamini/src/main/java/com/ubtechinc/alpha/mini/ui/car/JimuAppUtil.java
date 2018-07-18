package com.ubtechinc.alpha.mini.ui.car;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ubtechinc.alpha.mini.AlphaMiniApplication;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 * Created by riley.zhang on 2018/7/6.
 */

public class JimuAppUtil {
    private static final int JIMU_PACKAGE_ID = 36;
    private static final int JIMU_ROBOR_ID = 66235;

    public static void startJimuAPP() {
        String params = "{\"module\":\"build\",\"pacakageId\":\""+JIMU_PACKAGE_ID + "\",\"robotId\":\""+JIMU_ROBOR_ID+"\",\"source\":\"mini\",\"callback\":\"\"}";
        String jimuUri = "jimu://redirect/location?json="+params;
        Uri uri = Uri.parse(jimuUri);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        AlphaMiniApplication.getInstance().startActivity(intent);
    }

    public static void startJimuAppByUri(Activity activity) {

        try {
            String packageId = "";
            String robotId = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("packageId", packageId);
            jsonObject.put("robotId", robotId);
            jsonObject.put("module", "build");
            jsonObject.put("source", "mini");
            String json = jsonObject.toString();
            String encodeJson = URLEncoder.encode(json, "UTF-8");
            Intent intent = new Intent();
            intent.setData(Uri.parse("jimu://redirect/location?json=" + encodeJson));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isIntentAvailable(activity, intent)) {
                activity.startActivity(intent);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        if(intent == null || context == null){
            return false;
        }
        if(intent.resolveActivity(context.getPackageManager()) == null){
            return false;
        }
        return true;
    }
}
