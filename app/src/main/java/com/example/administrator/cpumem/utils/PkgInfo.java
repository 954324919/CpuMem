package com.example.administrator.cpumem.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017-07-10.
 */
public class PkgInfo {
    public MemInfo memInfo=new MemInfo();
    public ArrayList<String> curActivity=new ArrayList<String>();
    public String mypkgInfo(Context context){
        String pkgName=getCurProcPkgName(context);

        String appName=getPkgName(context);
        String pkgPid=String.valueOf(getCurPkgPid());
        String limit_heapgroth=memInfo.dalvikLimMem();  //应用可获取的最大内存
        String cpuName=getCpuName();
        String build_ver=String.valueOf(Build.VERSION.RELEASE);  //获取当前编译的sdk版本
        String brand= Build.BRAND;  //获取手机厂商信息
        String model=Build.MODEL;   //手机型号

        Log.i("Testing","应用包名:"+pkgName+",应用名称:"+appName+",应用pid:"+pkgPid+",应用可获取的最大内存:"+limit_heapgroth+"M,机器cpu型号:"+
                cpuName+",Android系统版本:"+build_ver+",手机厂商信息:"+brand+",手机型号:"+model);
        return null;
    }
    public String getCurProcPkgName(Context context){   //根据当前pid获取应用包名
        int pid=android.os.Process.myPid();  //获取到当前运行应用的pid值
        ActivityManager mActivityManager=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo appProcess:mActivityManager.getRunningAppProcesses()){
            if(appProcess.pid==pid){
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 获取当前应用pid
     * @return
     */
    public int getCurPkgPid(){
        return android.os.Process.myPid();
    }
    /*
    根据当前包名获取它的pid值
     */
    public int getPidByPackageName(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo runningProcess : run) {
            if ((runningProcess.processName != null)
                    && runningProcess.processName.equals(packageName)) {
                return runningProcess.pid;
            }
        }
        return 0;
    }
    /**
     * 获取设备通知栏高度
     */
    public int getStatusBarHeight(Context context) {
        // set status bar height to 25
        int barHeight = 25;
        Resources myres=context.getResources();
        int resourceId = myres.getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            barHeight = myres.getDimensionPixelSize(resourceId);
        }
        return barHeight;
    }
    public String getPkgName(Context context){  //获取应用的名字
        PackageManager packageManager=null;
        ApplicationInfo applicationInfo=null;
        String appName;
        try{
            packageManager=context.getPackageManager();
            applicationInfo=packageManager.getApplicationInfo(context.getPackageName(),0);
            appName=(String)packageManager.getApplicationLabel(applicationInfo);
            return appName;
        }catch(Exception e){e.printStackTrace();}
        return null;
    }
    public String getCpuName() {
        try {
            RandomAccessFile cpuStat = new RandomAccessFile("/proc/cpuinfo", "r");
            // check cpu type
            String line;
            while (null != (line = cpuStat.readLine())) {
                String[] values = line.split(":");
                if (values[0].contains("model name") || values[0].contains("Processor")) {
                    cpuStat.close();
                    return values[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public String getTopActivity(Context context) {
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // Note: getRunningTasks is deprecated in API 21(Official)
        //if (Build.VERSION.SDK_INT >= 21) {
        //    return "N/A";
        //}
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        if (runningTaskInfos != null)
            return (runningTaskInfos.get(0).topActivity).toString();
        else
            return "N/A";
    }
}
