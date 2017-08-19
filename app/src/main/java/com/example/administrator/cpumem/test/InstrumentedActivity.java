package com.example.administrator.cpumem.test;

import com.example.administrator.cpumem.MainActivity;

/**
 * Created by Jack on 2017-07-29.
 */
public class InstrumentedActivity extends MainActivity{
    public FinishListener mListener;
    public void setFinishListener(FinishListener listener){
        mListener=listener;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mListener!=null){
            mListener.onActivityFinished();
        }
    }
}
