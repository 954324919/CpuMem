package com.example.administrator.cpumem.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jack on 2017-07-14.
 */
public class UpdateReceiver extends BroadcastReceiver{
    public boolean isServiceStop;
    @Override
    public void onReceive(Context context, Intent intent) {
        isServiceStop=intent.getExtras().getBoolean("isServiceStop");
        if(isServiceStop){

        }
    }
}
