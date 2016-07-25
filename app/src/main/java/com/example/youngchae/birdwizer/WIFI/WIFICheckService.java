package com.example.youngchae.birdwizer.WIFI;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import com.example.youngchae.birdwizer.Floating.MyService;

public class WIFICheckService extends Service {
    WifiReceiver mWifiMonitor;
    public WIFICheckService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WifiCheckService","start");

        final SharedPreferences sharedPreferences = getSharedPreferences("wifi",MODE_PRIVATE);
        sharedPreferences.getAll();
        mWifiMonitor = new WifiReceiver(this);
        IntentFilter receiverFilter = new IntentFilter();
        receiverFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiverFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiMonitor, receiverFilter);

        mWifiMonitor.setOnChangeNetworkStatusListener(new WifiReceiver.OnChangeNetworkStatusListener() {
            @Override
            public void OnChanged(int status, WifiInfo networkInfo) {
                for(int i = 1 ; i<=sharedPreferences.getInt("size",0);i++){
                    if(sharedPreferences.getString("wifi_SSID_"+i,"없어").equals(networkInfo.getSSID().toString())){
//                        Toast.makeText(getApplicationContext(),"현재 연결 : "+ networkInfo.getSSID()+"name : "+sharedPreferences.getString("wifi_name_"+i," "), Toast.LENGTH_SHORT).show();
//                        Log.d("현재연결", networkInfo.getSSID());
                        Log.d("현재연결", sharedPreferences.getString("wifi_name_"+i," "));
                        Intent intent = new Intent(getApplicationContext(), MyService.class);
                        intent.putExtra("SSID",sharedPreferences.getString("wifi_name_"+i , " "));
                        startService(intent);
                    }
                }
            }
        });
    }
}
