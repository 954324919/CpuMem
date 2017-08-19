package com.example.administrator.cpumem.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017-07-14.
 */
public class DemoService extends Service {
    @Override
    public void onCreate(){
        Log.i("Testing","ceshi");
        super.onCreate();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
