package com.example.administrator.cpumem.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.administrator.cpumem.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.SimpleFormatter;

/**
 * Created by Jack on 2017-07-22.
 */
public class SaveResult {
    /**
     * 保存测试结果
     */
    public String resultFilePath="/sdcard/PerResult/";
    private FileOutputStream out;
    private OutputStreamWriter osw;
    private BufferedWriter bw;
    public MemInfo memInfo;
    public CpuInfo cpuInfo;
    public static final String COMMA=",";
    public static final String LINE_END="\r\n";
    public PkgInfo pkgInfo;
    public Context context;
    public SaveResult(Context context,MemInfo memInfo,CpuInfo cpuInfo,PkgInfo pkgInfo){   //构造方法，将得到的list列表通过构造方法传递过来
        this.memInfo=memInfo;
        this.cpuInfo=cpuInfo;
        this.context=context;
        this.pkgInfo=pkgInfo;
    }
    public void createRes(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String mDateTime=formatter.format(calendar.getTime().getTime());
        String resultFile=resultFilePath+mDateTime+".csv";
        try{
            File resFile=new File(resultFile);
            resFile.getParentFile().mkdirs();
            resFile.createNewFile();
            out=new FileOutputStream(resFile);
            osw=new OutputStreamWriter(out);
            bw=new BufferedWriter(osw);

            //从memInfo中获取mem信息
            ArrayList<String> mymemRatioList=memInfo.memRatioList;
            ArrayList<String> mycpuRatioList=cpuInfo.cpuRatioList;
            ArrayList<String> mytimestamp=memInfo.timestamp;
            ArrayList<String> mycurActivity=pkgInfo.curActivity;
            Log.i("Testing", "time列表:" + memInfo.timestamp.toString());
            Log.i("Testing", "activity列表:" + pkgInfo.curActivity.toString());
            Log.i("Testing", "mem列表:" + memInfo.memRatioList.toString());
            Log.i("Testing", "cpu列表:" + cpuInfo.cpuRatioList.toString());
            //写入设备的基本信息
            bw.write(this.context.getString(R.string.process_package) + COMMA + this.pkgInfo.getCurProcPkgName(this.context) + LINE_END +
                    this.context.getString(R.string.process_name) + COMMA +this.pkgInfo.getPkgName(this.context) + LINE_END +
                    this.context.getString(R.string.process_pid) + COMMA + String.valueOf(this.pkgInfo.getCurPkgPid()) + LINE_END +
                    this.context.getString(R.string.limit_heapgroth) + COMMA + memInfo.dalvikLimMem() + LINE_END +
                    this.context.getString(R.string.cpu_name) + COMMA + this.pkgInfo.getCpuName() + LINE_END +
                    this.context.getString(R.string.build_ver) + COMMA + String.valueOf(Build.VERSION.RELEASE) + LINE_END +
                    this.context.getString(R.string.brand) + COMMA + Build.BRAND + LINE_END +
                    this.context.getString(R.string.model) + COMMA + Build.MODEL + LINE_END);
            //将列表的每列标题title写入
            bw.write(this.context.getString(R.string.thecurTime)+COMMA+this.context.getString(R.string.topAct)+COMMA+
            this.context.getString(R.string.memRatio)+COMMA+this.context.getString(R.string.cpuRatio)+LINE_END);
            //写入列表中每一列的内容
            for(int i=0;i<mycpuRatioList.size();i++){
                bw.write(mytimestamp.get(i)+COMMA+mycurActivity.get(i)+COMMA+
                        mymemRatioList.get(i)+COMMA+mycpuRatioList.get(i)+LINE_END);
            }

            bw.close();
        }catch(Exception e){e.printStackTrace();}
    }
}
