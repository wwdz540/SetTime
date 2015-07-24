package com.winhands.widgets;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.winhands.bean.SntpClient;
import com.winhands.settime.R;
import com.winhands.util.SharePreferenceUtils;

import java.util.Calendar;
import java.util.Date;

public class TimerService extends Service implements Runnable {

    public static final String TAG="TSA";
    private static String ACTION_UPDATE_ALL="com.untas.UPDATE_ALL";

    private static String ACTION_SERVICE_STOP="com.untas.ACTION_SERVICE_STOP";
    private SharedPreferences sp;

    private static final int UPDATE_TIME = 1000;
    // 周期性更新 widget 的线程
    private Thread mUpdateThread;

    private Context mContext;
    Date netDate;
    Calendar netDAteCal;
    private TimerAppWidgetProvider appWidgetProvider = TimerAppWidgetProvider.getInstance();



    @Override
    public void onCreate() {
       // Log.d(TAG, "Service Createed==");
        sp = new SharePreferenceUtils(this).getSP();
        mContext = this.getApplicationContext();
        netDAteCal=Calendar.getInstance();
        initThread();
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

    private void initThread(){
        Log.d(TAG,"Pre Thread");
       new Thread(new Runnable() {
            @Override
            public void run() {
                getNetDate("1.cn.pool.ntp.org");
            }
        }).start();
    }
    private void getNetDate(String ip) {
        SntpClient client = new SntpClient();
        if(client.requestTime(ip,300)){


            long now = client.getNtpTime() + System.nanoTime() / 1000
                    - client.getNtpTimeReference();

            netDate = new Date(now
                    - ((8 - sp.getInt("timezone", 8)) * 60 * 60 * 1000));
            netDAteCal.setTime(netDate);
            Log.d(TAG,"netDateIs"+netDate);
            // 创建并开启线程UpdateThread
            mUpdateThread = new Thread(this);
            mUpdateThread.start();

        }
    }

    @Override
    public void run() {

        try {
            while (true) {

                netDAteCal.add(Calendar.SECOND,1);
                appWidgetProvider.setTime(mContext,netDAteCal.getTime());
                Thread.sleep(UPDATE_TIME);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
