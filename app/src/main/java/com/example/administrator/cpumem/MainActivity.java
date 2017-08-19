package com.example.administrator.cpumem;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.cpumem.service.MyPermanceService;
import com.example.administrator.cpumem.utils.CpuInfo;
import com.example.administrator.cpumem.utils.MemInfo;
import com.example.administrator.cpumem.utils.PkgInfo;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private Button btnTest,btnFloatTest;

    public static int OVERLAY_PERMISSION_REQ_CODE=1234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnTest=(Button)findViewById(R.id.test);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //PkgInfo pkgInfo=new PkgInfo();
                //pkgInfo.mypkgInfo(getApplicationContext());
                try{
                    Thread.sleep(2000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        btnFloatTest=(Button)findViewById(R.id.FloatTest);
        btnFloatTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PermissionsUtil.hasPermission(getApplicationContext(), Manifest.permission.SYSTEM_ALERT_WINDOW)){
                    Toast.makeText(getApplicationContext(), "打开悬浮窗权限", Toast.LENGTH_SHORT).show();
                }else{
                    PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {
                            Toast.makeText(getApplicationContext(),"用户授权打开悬浮窗权限",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permission) {
                            Toast.makeText(getApplicationContext(), "用户拒绝了打开悬浮窗权限", Toast.LENGTH_LONG).show();
                        }
                    },new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW});
                }
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, MyPermanceService.class);
                startService(intent);
                /**
                if(Build.VERSION.SDK_INT>=23){
                    if(Settings.canDrawOverlays(getApplicationContext())){
                    //存在权限
                        Log.i("Testing","存在权限");
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this, MyPermanceService.class);
                        startService(intent);
                    }
                    else{

                    Toast.makeText(getApplicationContext(),"不存在权限",Toast.LENGTH_SHORT).show();
                    try{
                        Intent intent=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:"+getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }catch (Exception e){e.printStackTrace();}
                    }
                }else{
                    if(Build.VERSION.SDK_INT>=21) {
                        long ts = System.currentTimeMillis();
                        UsageStatsManager statsManager = (UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
                        List<UsageStats> queryUsageStats = statsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
                        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
                            //如果为没有权限
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri=Uri.fromParts("package",getApplicationContext().getPackageName(),null);
                            intent.setData(uri);
                            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                            Log.i("Testing","开始申请权限");
                        }else{
                            Intent intent=new Intent();
                            intent.setClass(MainActivity.this, MyPermanceService.class);
                            startService(intent);
                        }
                    }else{
                        Intent intent=new Intent();
                        intent.setClass(MainActivity.this, MyPermanceService.class);
                        startService(intent);
                    }
                }*/
                /*
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, MyPermanceService.class);
                startService(intent);
                */
            }
        });

    }
    /*


    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==OVERLAY_PERMISSION_REQ_CODE){
            if(!Settings.canDrawOverlays(this)){
                Toast.makeText(this,"授权失败，无法打开悬浮窗",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"授权成功，已打开悬浮窗",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent();
                intent.setClass(MainActivity.this, MyPermanceService.class);
                startService(intent);
            }
        }
    }
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            Thread.sleep(1000);  //用来测试丢帧
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
