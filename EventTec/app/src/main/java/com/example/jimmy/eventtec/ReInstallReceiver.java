package com.example.jimmy.eventtec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Josue on 13/10/2017.
 */
public class ReInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,CustomService.class));
    }
}
