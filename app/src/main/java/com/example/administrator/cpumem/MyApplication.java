package com.example.administrator.cpumem;

import android.app.Application;
import android.view.Choreographer;

import com.example.administrator.cpumem.utils.AppBlockCancaryContext;
import com.example.administrator.cpumem.utils.FpsInfo;
import com.github.moduth.blockcanary.BlockCanary;

/**
 * Created by Jack on 2017-07-28.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        //在主进程初始化时调用
        //jacoco  android手工代码覆盖率统计
        BlockCanary.install(this,new AppBlockCancaryContext()).start();
        Choreographer.getInstance().postFrameCallback(new FpsInfo(System.nanoTime()));
    }
}
