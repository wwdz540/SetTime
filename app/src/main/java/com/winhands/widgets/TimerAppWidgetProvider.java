package com.winhands.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.winhands.settime.R;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by cheshire_cat on 15/7/17.
 */
public class TimerAppWidgetProvider  extends AppWidgetProvider {
    private static  final  String LOGTAG="TSA";

    private static TimerAppWidgetProvider instance;

    public static  TimerAppWidgetProvider getInstance(){
       if(instance==null){
           instance = new TimerAppWidgetProvider();
       }
        return  instance;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(LOGTAG,"on Update");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 第一个widget被创建时调用
    @Override
    public void onEnabled(Context context) {
        Log.d(LOGTAG, "onEnabled");
        // 在第一个 widget 被创建时，开启服务
        startService(context);
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(LOGTAG, "stopService");
        stopService(context);
        super.onDisabled(context);
    }


    private void startService(Context context){
      //   context.startService(new Intent(TimerService.class.getName()).setPackage("com.winhands.settime"));
       Intent intent = new Intent(context.getApplicationContext(),TimerService.class);
       context.startService(intent);
    }

    private  void stopService(Context context){
        Intent intent = new Intent(context.getApplicationContext(),TimerService.class);
        context.stopService(intent);
    }


     void setTime(Context context,Date date){
         AppWidgetManager  am= AppWidgetManager.getInstance(context);
        int[] widgetIds =  am.getAppWidgetIds(new ComponentName(context,this.getClass()));
         if(widgetIds.length!=0)
         updateAllAppWidgets(context,am,widgetIds,date);

     }


      void updateAllAppWidgets(Context context, AppWidgetManager manager, int[] ids,Date date) {


        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.timer_widget_1);
        for(Integer tmpAppId:ids){
            int appId=tmpAppId.intValue();

            // remoteView.setTextViewText(R.id.appwidget_text,DF.format(date));
            int h=date.getHours();
            remoteView.setImageViewResource(R.id.nb_h0,Utils.NUMBERS[h%10]);
            remoteView.setImageViewResource(R.id.nb_h1,Utils.NUMBERS[h/10]);

            int m=date.getMinutes();
            remoteView.setImageViewResource(R.id.nb_m0,Utils.NUMBERS[m%10]);
            remoteView.setImageViewResource(R.id.nb_m1,Utils.NUMBERS[m/10]);

            int s = date.getSeconds();
            remoteView.setImageViewResource(R.id.nb_s0,Utils.NUMBERS[s%10]);
            remoteView.setImageViewResource(R.id.nb_s1,Utils.NUMBERS[s/10]);

            manager.updateAppWidget(appId,remoteView);
        }
    }

}
