package com.example.administrator.cpumem.utils;

import android.util.Log;
import android.view.Choreographer;
import android.view.Surface;

/**
 * Created by Jack on 2017-08-05.
 */
public class FpsInfo implements Choreographer.FrameCallback {

    private long mLastFrameTimeNanos=0;  //实际执行当前frame的时间
    private long mFrameIntervalNanos;
    public FpsInfo(long lastFrameTimeNanos){
        mLastFrameTimeNanos=lastFrameTimeNanos;
        mFrameIntervalNanos=(long)(1000000000 / 60.0);  // 帧率，也就是渲染一帧的时间，getRefreshRate是刷新率，一般是60
         }
    @Override
    public void doFrame(long frameTimeNanos) {  //Vsync信号到来的时间frameTimeNanos
        if(mLastFrameTimeNanos==0){
            mLastFrameTimeNanos=frameTimeNanos;
        }
        final long jitterNanos=frameTimeNanos-mLastFrameTimeNanos;
        if(jitterNanos>=mFrameIntervalNanos){

            final long skippedFrames=jitterNanos/mFrameIntervalNanos;
            //Log.i("Testing","Skipped "+skippedFrames+" frames!");
            if(skippedFrames>60){  //设定丢帧率 60
                Log.i("Testing","Skipped "+skippedFrames+" frames!");
            }
        }
        mLastFrameTimeNanos=frameTimeNanos;
        //注册下一帧的回调
        Choreographer.getInstance().postFrameCallback(this);
    }
}
