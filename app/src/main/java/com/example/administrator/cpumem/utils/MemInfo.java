package com.example.administrator.cpumem.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Administrator on 2017-07-11.
 */
public class MemInfo {
    public ArrayList<String> memRatioList=new ArrayList<String>();
    public ArrayList<String> timestamp=new ArrayList<String>();
public long getFreeMemorySize(Context context){
    ActivityManager.MemoryInfo outInfo=new ActivityManager.MemoryInfo();
    ActivityManager am=(ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    am.getMemoryInfo(outInfo);
    long avaliMem=outInfo.availMem;
    //Log.i("Testing:设备的总内存",String.valueOf(outInfo.totalMem));
    //Log.i("Testing:设备可用内存",String.valueOf(outInfo.availMem));
    //Log.i("Testing:达到",String.valueOf(outInfo.threshold)+"就会触发LMK，系统开始杀进程了");
    //Log.i("Testing:现在的状态",String.valueOf(outInfo.lowMemory));
    return avaliMem/1024;
    }
public int getPidMemorySize(int pid,Context context){
    ActivityManager am=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    int[] myMempid=new int[] {pid};
    Debug.MemoryInfo[] memoryInfo=am.getProcessMemoryInfo(myMempid);
    memoryInfo[0].getTotalSharedDirty();
    int memSize=memoryInfo[0].getTotalPss();
    //Log.i("Testing:Dalvik的PSS值",String.valueOf(memoryInfo[0].dalvikPss));
    //Log.i("Testing:程序的PSS值,应用占用内存", String.valueOf(memoryInfo[0].getTotalPss()));
    //Log.i("Testing:程序的SharedDirty值",String.valueOf(memoryInfo[0].getTotalSharedDirty()));
    //Log.i("Testing:程序的PrivateDirty",String.valueOf(memoryInfo[0].getTotalPrivateDirty()));

    //memoryInfo[0].getMemoryStat("com");
    //Log.i("Testing:程序的PrivateClean",String.valueOf(memoryInfo[0].getTotalPrivateClean()));
    //Log.i("Testing:程序的SharedClean值",String.valueOf(memoryInfo[0].getTotalSharedClean()));
    return memSize;
    }
    public static String runCMD(String cmdString) {

        ProcessBuilder execBuilder = null;
        execBuilder = new ProcessBuilder("su", "-c", cmdString);
        //execBuilder.redirectErrorStream(true);

        Process exec = null;
        try {
            exec = execBuilder.start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        InputStream is = exec.getInputStream();

        String result = "";
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    is));
            while ((line = br.readLine()) != null) {
                result += line;
                result += "\r\n";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void parseing(int pid){
        boolean infoStart=false;
        String[][] heapData = new String[2][2];
        try {
            //Process p = Runtime.getRuntime().exec(" dumpsys meminfo "+pid);


            Runtime runtime = Runtime.getRuntime();
            Process p = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("dumpsys meminfo " + pid + "\n");
            os.writeBytes("exit\n");
            os.flush();
            String resultString=null;
            //resultString=runCMD("dumpsys meminfo " +pid);
            //Log.i("Testing",resultString);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                Log.i("Testing",line);
                line = line.trim();
                if (line.contains("Permission Denial")) {
                    Log.i("Testing","fail");
                    break;
                } else {
                    // 当读取到MEMINFO in pid 这一行时，下一行就是需要获取的数据
                    Log.i("Testing","进入");
                    if (line.contains("MEMINFO in pid")) {
                        infoStart = true;
                    } else if (infoStart) {
                        String[] lineItems = line.split("\\s+");
                        int length = lineItems.length;
                        Log.i("Testing:line is",line);
                        if (line.startsWith("size")) {
                            heapData[0][0] = lineItems[1];
                            heapData[1][0] = lineItems[2];
                        } else if (line.startsWith("allocated")) {
                            heapData[0][1] = lineItems[1];
                            heapData[1][1] = lineItems[2];
                            break;
                        } else if (line.startsWith("Native")) {
                            Log.d("Testing", "Native");

                            Log.d("Testing", "lineItems[4]=" + lineItems[4]);
                            Log.d("Testing", "lineItems[5]=" + lineItems[5]);
                            heapData[0][0] = lineItems[length - 3];
                            heapData[0][1] = lineItems[length - 2];
                        } else if (line.startsWith("Dalvik")) {
                            Log.d("Testing", "Dalvik");
                            Log.d("Testing", "lineItems[4]=" + lineItems[4]);
                            Log.d("Testing", "lineItems[5]=" + lineItems[5]);
                            heapData[1][0] = lineItems[length - 3];
                            heapData[1][1] = lineItems[length - 2];
                            break;
                        }
                    }
                }
            }
        }catch(Exception e){
                e.printStackTrace();
            }
        }
    public String dalvikLimMem(){
        String dalLiMem="";
        String buildPath="/system/build.prop";
        try{
            RandomAccessFile buildInfo=new RandomAccessFile(buildPath,"r");
            String line="";
            String dalviklimitgrowth="";
            while ((line = buildInfo.readLine()) != null) {
                if(line.contains("dalvik.vm.heapgrowthlimit")) {
                    dalviklimitgrowth=line;
                    break;
                }
            }

            String[] tok = dalviklimitgrowth.split("=");
            String mydal= tok[1];
            String[] tok1=mydal.split("m");
            dalLiMem=tok1[0];
            Log.i("Testing:应用可以申请的最大内存", dalLiMem+"M");
            buildInfo.close();
        }catch (Exception e){
            Log.e("Testing process","获取应用cpu time失败");
            e.printStackTrace();
        }
        return dalLiMem;
    }

    public long dalLi=Long.parseLong(dalvikLimMem());

    public String memRatioInfo(int pid,Context context){
        String percent="N/A";
        try {
            DecimalFormat fomart = new DecimalFormat();
            fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            fomart.setGroupingUsed(false);
            fomart.setMaximumFractionDigits(2);
            fomart.setMinimumFractionDigits(2);

            int processMem = getPidMemorySize(pid, context);
            percent = fomart.format(((double) processMem / ((double) dalLi*1024)) * 100);
            Log.i("Testing:dalvik.vm.heapgrowthlimit",dalvikLimMem());
            Log.i("Testing:应用程序当前内存", String.valueOf(processMem));
            Log.i("Testing:应用内存占用百分比",percent);
        }catch(Exception e){
            e.printStackTrace();
        }
        return percent;
    }
}
