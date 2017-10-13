package com.example.jimmy.eventtec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jimmy on 12/05/2016.
 */
public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    public static boolean runing = false;
    public static String ip="http://172.19.32.10/infoTec/";//direcion del servidor
    @Override
    public void onReceive(final Context context, Intent intent) {

        context.startService(new Intent(context, CustomService.class));
    }
}