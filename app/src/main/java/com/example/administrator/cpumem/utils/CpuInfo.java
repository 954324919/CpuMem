package com.example.administrator.cpumem.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by Administrator on 2017-07-10.
 */
public class CpuInfo {
    private Context context;
    private int pid;
    private long procCpu;
    private String topProCpu;
    //private ArrayList<Long> idleCpu=new ArrayList<Long>();
    private ArrayList<Long> totalCpuTime=new ArrayList<Long>();  //总的cpu  time
    private ArrayList<Long> processCpuTime=new ArrayList<Long>();  //应用cpu  time
    private ArrayList<Long> totalCpuTime2=new ArrayList<Long>();  //总的cpu  time2  之所以定义这个是因为每次 取值 都会“叠加”
    private ArrayList<Long> processCpuTime2=new ArrayList<Long>();  //应用cpu  time2 之所以定义这个是因为每次 取值 都会“叠加”
    private ArrayList<String> totalCpuRatio = new ArrayList<String>();
    public ArrayList<String> cpuRatioList=new ArrayList<String>();
    private PkgInfo pkgInfo;
    public CpuInfo(Context context,int pid){
        this.context=context;
        this.pid=pid;
        pkgInfo=new PkgInfo();
    }

    public void DemoTest(){
        int i=0;
        //readTotalCpuStat();
        //readProcessCpuStat();

        getCpuRatioCmd();
        //getCpuRatioInfo();
        //topCpu();
    }
    /*
    获取到cpu 总时间  total cpu time
    */
    private void readTotalCpuStat(){
        try{
            RandomAccessFile cpuInfo=new RandomAccessFile("/proc/stat","r");  //
            String line = "";
            /*
            while ((null != (line = cpuInfo.readLine())) && line.startsWith("cpu")) {
                String[] toks = line.split("\\s+");
                idleCpu.add(Long.parseLong(toks[4]));
                totalCpu.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                        + Long.parseLong(toks[6]) + Long.parseLong(toks[5]) + Long.parseLong(toks[7]));
            }
            */
            line=cpuInfo.readLine();  //读取第一行，表示的几个 核的cpu  time 总和
            String[] toks = line.split("\\s+");
            totalCpuTime.add(Long.parseLong(toks[1]));  //将得到的几个核的cpu time 记录到列表中

            cpuInfo.close();
            Log.i("Testing:Total CPU TIME", totalCpuTime.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /*
       根据要测试的pid值  获取到这个应用的 cpu time
     */
    private void readProcessCpuStat(){
        String processid=Integer.toString(pid);
        String cpuStatPath="/proc/"+processid+"/stat";
        try{
            RandomAccessFile processCpuInfo=new RandomAccessFile(cpuStatPath,"r");
            String line="";
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            procCpu = Long.parseLong(tok[11])+Long.parseLong(tok[12])+Long.parseLong(tok[13]) + Long.parseLong(tok[14]);  //包含  11  12  13  14位置

            processCpuTime.add(procCpu);   //将应用cpu time  加入到列表中
            Log.i("Testing:Process Cpu", processCpuTime.toString());
            processCpuInfo.close();
        }catch (Exception e){
            Log.e("Testing process","获取应用cpu time失败");
            e.printStackTrace();
        }
    }
    private ArrayList<String> getCpuRatioInfo() {
        try {
            DecimalFormat fomart = new DecimalFormat();
            fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            fomart.setGroupingUsed(false);
            fomart.setMaximumFractionDigits(2);
            fomart.setMinimumFractionDigits(2);
            String cpuRatio = "0.00";
            int i=0;

            Log.i("Testing:totalCpuTime2", totalCpuTime2.toString());
            Log.i("Testing:totalCpuTime", totalCpuTime.toString());
            Log.i("Testing:geting", totalCpuRatio.toString());
        } catch (Exception e) {
            Log.i("Testing:getCpuRatioInfo", "获取cpu百分比出错" + e.getMessage());
        }
        return totalCpuRatio;
    }
    public String getCpuRatioCmd(){
        try{

            Process p=Runtime.getRuntime().exec(" top -n 1");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(p.getInputStream()));

            //RandomAccessFile bufferedReader=new RandomAccessFile(" top -n 1","r");

            String line="";
            String newline="";
            String myline="";
            String pkgName=pkgInfo.getCurProcPkgName(context);
            while(null!=(line=bufferedReader.readLine())){

                if (line.contains(pkgName)){
                    newline=line.trim();
                    break;
                }else{
                    myline=myline+line;
                }
            }
            Log.i("Testing: pkgName", pkgName);
            String[] tok=newline.split("\\s+");   //   \\s+表示空格
            Log.i("Testing:newline Cpu", Arrays.toString(tok));
            topProCpu=String.valueOf(tok[2]);  //获取top 命令中的第三个位置
            String[] cpuUsage=topProCpu.split("%");
            //Long cpu=Long.parseLong(cpuUsage[0]);

            Log.i("Testing:应用CPU占用百分比", cpuUsage[0]);
            bufferedReader.close();
            return cpuUsage[0];
        }catch(Exception e){
            e.printStackTrace();
        }
        return "N/A";
    }
}
