package com.example.administrator.cpumem.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.cpumem.R;
import com.example.administrator.cpumem.utils.CpuInfo;
import com.example.administrator.cpumem.utils.MemInfo;
import com.example.administrator.cpumem.utils.PkgInfo;
import com.example.administrator.cpumem.utils.SaveResult;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Administrator on 2017-07-13.
 */
public class MyPermanceService extends Service {
    private MemInfo memInfo;
    private CpuInfo cpuInfo;
    private WindowManager windowManager=null;  //应用悬浮窗
    private WindowManager.LayoutParams wmParams=null;
    private View viFloatingWindow;
    private float x; //用于拖拽时记录浮窗的x值
    private float y; //用于拖拽时记录浮窗的y
    private float mTouchStartX; //触摸的开始x值
    private float mTouchStartY;  //触摸的开始y值
    private int statusBarHeight;  //通知栏的高度
    private PkgInfo pkgInfo;
    private boolean isFloating;  //是否开启悬浮窗
    private TextView txtMem_info;  //应用已使用的内存/剩余内存/内存使用率
    private TextView txtCpu_info; //应用cpu占用率
    private TextView txtTraffic; //应用流量统计
    private Button btnWifi; //wifi开关
    private Button btnStop; //测试开关
    private WifiManager wifiManager; //wifi管理
    private boolean isCollect=false;  //是否进行循环收集数据
    private boolean isSet=false;  //设置是否关闭收集并保存结果
    private int UPDATE_PIC=1;
    private Handler handler=new Handler();   //创建handler发送更新界面消息
    private int pid, uid;
    private int delaytime=2;  //收集数据时的频率   单位 秒
    private DecimalFormat fomart;

    public static final String SERVICE_ACTION = "Service Action";
    @Override
    public void onCreate(){
        Log.i("Testing", "Service is start");
        super.onCreate();
        memInfo=new MemInfo();
        wmParams=new WindowManager.LayoutParams();
        pkgInfo=new PkgInfo();
        pid=pkgInfo.getCurPkgPid();
        cpuInfo=new CpuInfo(getBaseContext(),pid);
        statusBarHeight=pkgInfo.getStatusBarHeight(getApplicationContext());
        isFloating=true;
        isCollect=true;
        isSet=true;

        fomart = new DecimalFormat();
        fomart.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        fomart.setGroupingUsed(false);
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(0);

    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        initFloatWindow();
        createFloatingWindow();

        handler.postDelayed(task, 1000);
        return START_NOT_STICKY;
    }

    private void initFloatWindow(){
        if(isFloating){
            viFloatingWindow= LayoutInflater.from(this).inflate(R.layout.floating,null); //创建自定义的浮窗控件
            txtMem_info=(TextView)viFloatingWindow.findViewById(R.id.memuse_info);
            txtCpu_info=(TextView)viFloatingWindow.findViewById(R.id.cpu_info);
            txtTraffic=(TextView)viFloatingWindow.findViewById(R.id.traffic);
            btnWifi = (Button) viFloatingWindow.findViewById(R.id.wifi);
            btnStop = (Button) viFloatingWindow.findViewById(R.id.stop);

            wifiManager=(WifiManager)getSystemService(Context.WIFI_SERVICE);
            if(wifiManager.isWifiEnabled()) {
                btnWifi.setText(R.string.close_wifi);
            }else{
                btnWifi.setText(R.string.open_wifi);
            }
            if(isCollect) {
                btnStop.setText(R.string.stop_test);
            }else{
                btnStop.setText(R.string.start_test);
            }
            btnWifi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        btnWifi=(Button)viFloatingWindow.findViewById(R.id.wifi);

                        String btnText=(String) btnWifi.getText();
                        String wifiText=getResources().getString(R.string.open_wifi);
                        if(btnText.equals(wifiText)) {
                            wifiManager.setWifiEnabled(true);
                            btnWifi.setText(getString(R.string.close_wifi));
                        } else {
                            wifiManager.setWifiEnabled(false);
                            btnWifi.setText(getString(R.string.open_wifi));
                        }
                    }catch(Exception e){e.printStackTrace();
                        Toast.makeText(viFloatingWindow.getContext(),R.string.wifi_fail_toast,Toast.LENGTH_SHORT).show();}
                }
            });
            txtMem_info.setText(getString(R.string.loading));
            txtMem_info.setTextColor(Color.RED);
            txtTraffic.setTextColor(Color.RED);
            txtCpu_info.setTextColor(Color.RED);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnStop=(Button)viFloatingWindow.findViewById(R.id.stop);
                    String textBtnStop = (String) btnStop.getText();
                    if (textBtnStop.equals(getString(R.string.stop_test))) {
                        //点击按钮开始关闭
                        isCollect = false;

                        btnStop.setText(getString(R.string.start_test));
                        windowManager.updateViewLayout(viFloatingWindow, wmParams);
                        /*
                        Intent intent=new Intent();
                        intent.putExtra("isServiceStop",true);
                        intent.setAction(SERVICE_ACTION);
                        sendBroadcast(intent);
                        */
                        //stopSelf();   //服务停止则自身也结束
                    } else {
                        //开始收集数据
                        isCollect = true;

                        btnStop.setText(getString(R.string.stop_test));
                        windowManager.updateViewLayout(viFloatingWindow, wmParams);
                    }

                    /**
                     Intent intent=new Intent();
                     intent.putExtra("isServiceStop",true);
                     intent.setAction(SERVICE_ACTION);
                     sendBroadcast(intent);
                     stopSelf();   //服务停止则自身也结束
                     */
                }
            });
        }
    }
    private void createFloatingWindow(){
        SharedPreferences sharedPreferences=getSharedPreferences("float_flag", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt("float", 1);
        editor.commit();   //创建悬浮窗显示功能
        //获取WindowManager
        windowManager=(WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //类型
        wmParams.type=WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //flags
        wmParams.flags|=8;

        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        //设置浮窗显示宽高
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        windowManager.addView(viFloatingWindow, wmParams);

        viFloatingWindow.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                x = event.getRawX();
                y = event.getRawY() - statusBarHeight;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //放下
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP: //按住
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;  //记录初始 至为0
                        break;
                }
                return true;
            }
        });
    }

    private Runnable task=new Runnable() {
        @Override
        public void run() {
            if (isSet) {
                if (isCollect) {
                    dataRefresh(); //更新浮窗界面数据,并将统计的cpu、内存信息加到列表中
                } else {
                    //通过判断列表是否为空来进行是否要保存到excel中
                    if(memInfo.memRatioList.size()!=0 &&cpuInfo.cpuRatioList.size()!=0) {
                        SaveResult sr=new SaveResult(getApplicationContext(),memInfo,cpuInfo,pkgInfo);
                        sr.createRes();
                    }
                        //获取到列表中的数据然后保存到excel中
                        memInfo.memRatioList.clear();
                        cpuInfo.cpuRatioList.clear();
                }
                handler.postDelayed(this, delaytime);
                if (isFloating & viFloatingWindow != null) {
                    windowManager.updateViewLayout(viFloatingWindow, wmParams);
                }
            }
        }
                /**
                Intent intent=new Intent();
                intent.putExtra("isServiceStop", true);
                intent.setAction(SERVICE_ACTION);
                sendBroadcast(intent);
                stopSelf();
                 */

    };
    private String curTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String mDateTime = formatter.format(cal.getTime().getTime());
        return mDateTime;
    }
    private void dataRefresh(){
        int pidMemory=memInfo.getPidMemorySize(pid, getBaseContext());
        //long freeMemory=memInfo.getFreeMemorySize(getBaseContext());
        //String myfreeMemory=fomart.format((double) freeMemory / 1024);
        String processMemory=fomart.format((double)pidMemory/1024);
        String pidMemRatio=memInfo.memRatioInfo(pid,getBaseContext());  //应用内存占用率

        String cpuUsage=cpuInfo.getCpuRatioCmd();
        String curTimes=curTime();
        String curAct=pkgInfo.getTopActivity(getApplicationContext());
        memInfo.timestamp.add(curTimes);
        pkgInfo.curActivity.add(curAct);

        memInfo.memRatioList.add(processMemory); //添加内存信息到内存列表
        cpuInfo.cpuRatioList.add(cpuUsage);   //添加cpu信息到cpu列表

        if(isFloating){
            /*
            String btnText=(String) btnWifi.getText();
            String wifiText=getString(R.string.open_wifi);
            if(){
                btnWifi.setText(getString(R.string.close_wifi));
            }else{
                btnWifi.setText(getString(R.string.open_wifi));
            }
            String textBtnStop = (String) btnStop.getText();
            if (textBtnStop.equals(getString(R.string.stop_test))) {
                btnStop.setText(getString(R.string.start_test));
            }else{
                btnStop.setText(getString(R.string.stop_test));
            }*/
            String processCpuRatio = "0.00";
            String totalCpuRatio = "0.00";
            String trafficSize = "0";
            long tempTraffic = 0L;
            boolean isMb = false;
            if(!pidMemRatio.equals("") &&!processMemory.equals("") && cpuUsage!=null){
                txtMem_info.setText(getString(R.string.mem_info)+":"+processMemory+"/"+pidMemRatio);
                txtCpu_info.setText(getText(R.string.cpu_info)+":"+cpuUsage);
                txtTraffic.setText(getString(R.string.traffic_info) + ":XX");


            }
            windowManager.updateViewLayout(viFloatingWindow,wmParams);
        }else{
            windowManager.addView(viFloatingWindow, wmParams);
        }
    }
    /**
     * 更新view控件
     */
    private void updateViewPosition(){
        wmParams.x=(int) (x-mTouchStartX);
        wmParams.y=(int) (y-mTouchStartY);
        if(viFloatingWindow!=null){
            windowManager.updateViewLayout(viFloatingWindow,wmParams);
        }
    }
    public void closeOpenedStream(){  //关闭所有读写操作
        try{
            Log.i("Testing","test");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy(){
        if(windowManager!=null){
            windowManager.removeView(viFloatingWindow);
            viFloatingWindow=null;
        }
        handler.removeCallbacks(task);  //将task任务删除掉
        closeOpenedStream();
        isCollect=false;
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
