package com.ubtechinc.alpha.mini.avatar;


import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {

    private static TimerManager instance;

    public static TimerManager getInstance() {
        if (instance == null) {
            synchronized (TimerManager.class) {
                if (instance == null) {
                    instance = new TimerManager();
                }
            }
        }
        return instance;
    }

    private Timer timer;

    private TimerManager() {
        init();
    }

    public void schedule(TimerTask task, long delay, long period) {
        timer.schedule(task, delay, period);
    }

    public void schedule(TimerTask task, long delay) {
        timer.schedule(task, delay);
    }


    private void init() {
        timer = new Timer();
    }

    public void shutdown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        instance = null;
    }
}
