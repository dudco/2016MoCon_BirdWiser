package com.example.youngchae.birdwizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver extends BroadcastReceiver {
    public final static int WIFI_STATE_DISABLED = 0x00;
    public final static int WIFI_STATE_DISABLING = WIFI_STATE_DISABLED + 1;
    public final static int WIFI_STATE_ENABLED = WIFI_STATE_DISABLING + 1;
    public final static int WIFI_STATE_ENABLING = WIFI_STATE_ENABLED + 1;
    public final static int WIFI_STATE_UNKNOWN = WIFI_STATE_ENABLING + 1;
    public final static int NETWORK_STATE_CONNECTED = WIFI_STATE_UNKNOWN + 1;
    public final static int NETWORK_STATE_CONNECTING = NETWORK_STATE_CONNECTED + 1;
    public final static int NETWORK_STATE_DISCONNECTED = NETWORK_STATE_CONNECTING + 1;
    public final static int NETWORK_STATE_DISCONNECTING = NETWORK_STATE_DISCONNECTED + 1;
    public final static int NETWORK_STATE_SUSPENDED = NETWORK_STATE_DISCONNECTING + 1;
    public final static int NETWORK_STATE_UNKNOWN = NETWORK_STATE_SUSPENDED + 1;

    Context context;
    Intent serviceintent;

    public WifiReceiver() {
    }

    public interface OnChangeNetworkStatusListener
	{
		public void OnChanged(int status, WifiInfo networkInfo);
	}

    private WifiManager m_WifiManager = null;
    private ConnectivityManager m_ConnManager = null;
    private OnChangeNetworkStatusListener m_OnChangeNetworkStatusListener = null;

    public WifiReceiver(Context context)
    {
        this.context = context;
        m_WifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        m_ConnManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

	public void setOnChangeNetworkStatusListener(OnChangeNetworkStatusListener listener)
	{
		m_OnChangeNetworkStatusListener = listener;
	}
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String strAction = intent.getAction();
        if (strAction.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            switch (m_WifiManager.getWifiState()){
                //와이파이 꺼짐
                case WifiManager.WIFI_STATE_DISABLED : Log.d("WIFIState ", "DISABLED"); break;
                //와이파이 켜짐
                case WifiManager.WIFI_STATE_ENABLED : Log.d("WIFIState ", "ENABLE \nD"+m_WifiManager.getConnectionInfo()); break;
                //와이파이 꺼지는 중
                case WifiManager.WIFI_STATE_DISABLING: Log.d("WIFIState ","DISABLING"); break;
                //와이파이 켜지는 중
                case WifiManager.WIFI_STATE_ENABLING: Log.d("WIFIState", "ENABLING"); break;
            }

        }else if(strAction.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            NetworkInfo networkInfo = m_ConnManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable() == true){
//                if(networkInfo.getState() == NetworkInfo.State.CONNECTING){
//                    m_OnChangeNetworkStatusListener.OnChanged(NETWORK_STATE_CONNECTING, m_WifiManager.getConnectionInfo());
//                    Log.d("NetworkState ", "CONNECTING");
                if(networkInfo.getState() == NetworkInfo.State.CONNECTED)
                    Log.d("NetworkState ", "CONNECTED \n"+m_WifiManager.getConnectionInfo().toString());
                    m_OnChangeNetworkStatusListener.OnChanged(NETWORK_STATE_CONNECTED, m_WifiManager.getConnectionInfo());
//                }else if(networkInfo.getState() == NetworkInfo.State.DISCONNECTED){
//                    Log.d("NetworkState ", "DISCONNECTED");
//                    m_OnChangeNetworkStatusListener.OnChanged(NETWORK_STATE_DISCONNECTED, m_WifiManager.getConnectionInfo());
//                }else if(networkInfo.getState() == NetworkInfo.State.DISCONNECTING){
//                    Log.d("NetworkState ", "DISCONNECTING");
//                }
            }
        }
    }
}
