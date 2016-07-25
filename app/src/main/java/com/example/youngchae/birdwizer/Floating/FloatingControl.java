package com.example.youngchae.birdwizer.Floating;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.example.youngchae.birdwizer.R;
import com.example.youngchae.birdwizer.Utils;

/**
 * Created by youngchae on 2016-07-15.
 */
public class FloatingControl extends AppCompatActivity {
    Intent intent;
    SharedPreferences floatingInfo;
    SharedPreferences.Editor editor;
    int OVERLAY_PERMISSION_REQ_CODE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.floating_controler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_floating);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("플로팅 관리하기");

        floatingInfo = getSharedPreferences("floating", MODE_PRIVATE);
        editor = floatingInfo.edit();

//        intent = new Intent(FloatingControl.this, FloatingService.class);
        intent = new Intent(FloatingControl.this, MyService.class);
        SwitchCompat switchCompat = (SwitchCompat)findViewById(R.id.floationg_onandoff);

        switchCompat.setChecked(isServiceRunningCheck());
        editor.putBoolean("isShow", isServiceRunningCheck());

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    Log.d("Checked Status" , ""+b);
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Utils.canDrawOverlays(getApplicationContext())) {
                            editor.putBoolean("isShow", true);
                            editor.apply();
                            startService(intent);
                        } else {
                            requestPermission(OVERLAY_PERMISSION_REQ_CODE);
                        }
                    }else{
                        startService(intent);
                    }
                }else{
                    Log.d("Checked Status" , ""+b);
                    editor.putBoolean("isShow", false);
                    editor.apply();
                    stopService(intent);
                }
            }
        });
     }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Utils.canDrawOverlays(getApplicationContext())) {
                requestPermission(requestCode);
            } else {
                startChatHead();
            }
        }
    }
    private void startChatHead(){
        startService(intent);
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.youngchae.birdwizer.Floating.MyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
