package com.ubtechinc.alpha.mini.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    private static final SimpleDateFormat dateDf = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat monthDateDf = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat monthDateDfvedio = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat timeDf = new SimpleDateFormat("HH:mm:ss");
    private static SimpleDateFormat sEnglishDateFormat = new SimpleDateFormat("MMM. d", Locale.getDefault());

    public static String getDate(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日",
                Locale.CHINA);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(time);//有异常要捕获
            return sdr.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "1970年1月1日";
        }
    }

    public static String getDateFormat(String time){
        Calendar c = Calendar.getInstance();//可以对每个时间域单独修改
        int year = c.get(Calendar.YEAR);
        String formatTime =  getDate(time);
        if(String.valueOf(year).equals(formatTime.substring(0,4))){
            return formatTime.substring(5);
        }
        return formatTime ;

    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentTime(String formate) {
        SimpleDateFormat formatter = new SimpleDateFormat(formate);
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    public static String getAcitonTime(long time) {
        String text = "";
        if (time < 60) {
            text = "00:" + time;
        }
        if (time > -60 && time < 600) {
            if (time % 60 < 10) {
                text = "0" + time / 60 + ":" + "0" + time % 60;
            } else {
                text = "0" + time / 60 + ":" + time % 60;
            }
        }
        if (time > 600 && time < 3600) {
            if (time % 60 < 10) {
                text = time / 60 + ":" + "0" + time % 60;
            } else {
                text = time / 60 + ":" + time % 60;
            }
        }
        return text;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getMsgTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 判断字符串是否为纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str)
            ;
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Boolean isCorrectUserFormat(String str) {
        return true;
        // Boolean bl = false;
        // //首先,使用Pattern解释要使用的正则表达式，其中^表是字符串的开始，$表示字符串的结尾。
        // Pattern pt = Pattern.compile("^[a-zA-Z][0-9a-zA-Z_]+$");
        // //然后使用Matcher来对比目标字符串与上面解释得结果
        // Matcher mt = pt.matcher(str);
        // //如果能够匹配则返回true。实际上还有一种方法mt.find()，某些时候，可能不是比对单一的一个字符串，
        // //可能是一组，那如果只要求其中一个字符串符合要求就可以用find方法了.
        // if(mt.matches()){
        // bl = true;
        // }
        // return bl;
    }

    /**
     * 检查是否由数字或者字母构成
     *
     * @param str
     * @return
     */
    public static Boolean isLetterOrDigital(String str) {
        String str2 = "^[a-zA-Z0-9]+$";
        Pattern p = Pattern.compile(str2);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean countYears(String compareStr) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String currentTime = localSimpleDateFormat.format(curDate);
        int correctYear = Integer.valueOf(currentTime.split("-")[0]) - 13;
        String corretTime = currentTime.replace(currentTime.split("-")[0], correctYear + "");
        try {
            Date correctDate = localSimpleDateFormat.parse(corretTime);
            Date compareDate = localSimpleDateFormat.parse(compareStr);
            if (compareDate.before(correctDate) || compareDate.equals(correctDate)) {
                return true;
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return false;
    }


    /**
     * 给用户名返回JID
     *
     * @param jidFor   域名//如ahic.com.cn
     * @param userName
     * @return
     */
    public static String getJidByName(String userName, String jidFor) {
        return userName + jidFor;
    }

    /**
     * 给用户名返回JID,使用默认域名ahic.com.cn
     *
     * @param userName
     * @return
     */
    public static String getJidByName(String userName) {
        String jidFor = "@service.ubt.openfire";
        return getJidByName(userName, jidFor);
    }

    /**
     * 判断网络是否可用
     *
     * @param context
     * @param isCheckNetwork 是否需要检查网络
     * @return
     */
    public static boolean isNetworkConnected(Context context, boolean isCheckNetwork) {
        if (isCheckNetwork) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }
        return true;
    }



    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1500) {
            Log.d("time", "timeD==" + timeD);
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static void saveImageToAblum(Context context, File file) {

        

         /*将数据插入多媒体数据库中，相册会自动刷新界面显示新增的相册*/
        ContentValues localContentValues = new ContentValues();

        localContentValues.put("_data", file.toString());

        localContentValues.put("mime_type", "image/jpeg");

        ContentResolver localContentResolver = context.getContentResolver();

        Uri localUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        localContentResolver.insert(localUri, localContentValues);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public static void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }

    }

    public static boolean IsToday(String day)  {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
            return false ;
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }
    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
