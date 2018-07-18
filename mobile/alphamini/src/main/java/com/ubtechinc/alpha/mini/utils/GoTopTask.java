package com.ubtechinc.alpha.mini.utils;

import android.os.AsyncTask;
import android.widget.ListView;

/**
 * Created by hongjie.xiang on 2017/11/23.
 */

public class GoTopTask extends AsyncTask<Integer, Integer, String> {
    private int time ;
    public ListView lv;
    @Override
    protected void onPreExecute() {
        //回到顶部时间置0  此处的时间不是侠义上的时间
        time=0;
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(Integer... params) {
        // TODO Auto-generated method stub

        for(int i=params[0];i>=0;i--){
            publishProgress(i);
            //返回顶部时间耗费15个item还没回去，则直接去顶部
            //目的：要产生滚动的假象，但也不能耗时过多
            time++;
            if(time>15){
                publishProgress(0);
                return null;
            }
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... values) {
        lv.setSelection(values[0]);
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
    @Override
    protected void onCancelled() {
        // TODO Auto-generated method stub
        super.onCancelled();
    }

}
