package com.winhands.widgets;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.winhands.settime.R;

import java.util.Date;

public class TimerService extends Service implements Runnable {

    private static final String TAG="TSA";
    private static String ACTION_UPDATE_ALL="com.untas.UPDATE_ALL";

    private static String ACTION_SERVICE_STOP="com.untas.ACTION_SERVICE_STOP";

    private static final int UPDATE_TIME = 1000;
    // 周期性更新 widget 的线程
    private Thread mUpdateThread;
    private Context mContext;
    private TimerAppWidgetProvider appWidgetProvider = TimerAppWidgetProvider.getInstance();



    @Override
    public void onCreate() {
        Log.d(TAG, "Service Createed==");
        mContext = this.getApplicationContext();
        // 创建并开启线程UpdateThread
        mUpdateThread = new Thread(this);
        mUpdateThread.start();

        super.onCreate();
    }

    @Override
    public void onDestroy(){
        // 中断线程，即结束线程。
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
        }

        mContext.sendBroadcast(new Intent(ACTION_SERVICE_STOP));
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * 服务开始时，即调用startService()时，onStartCommand()被执行。
     * onStartCommand() 这里的主要作用：
     * (01) 将 appWidgetIds 添加到队列sAppWidgetIds中
     * (02) 启动线程
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         Notification notification = new Notification(R.drawable.ic_app_lg,
         getString(R.string.app_name), System.currentTimeMillis());

         PendingIntent pendingintent = PendingIntent.getActivity(this, 0,
         new Intent(), 0);
         notification.setLatestEventInfo(this, "时间服务", "时间服务",
         pendingintent);
         startForeground(0x111, notification);**/
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void run() {

        try {
            while (true) {
                Log.d(TAG,"run date");
                Date now = new Date();
                appWidgetProvider.setTime(mContext,now);
                Thread.sleep(UPDATE_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
