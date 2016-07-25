package com.example.youngchae.birdwizer.WIFI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.youngchae.birdwizer.R;

/**
 * Created by youngchae on 2016-07-15.
 */
public class WifiControl extends AppCompatActivity {
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

    public ArrayAdapter<String> spinnerAdapter;
    Toolbar wifiToolbar;
    String name;
    WifiReceiver mWifiMonitor;
    View dialogView;
    WifiManager m_WifiManager;
    int asdf = 1;
    SharedPreferences wifi;
    WifiControlerListAdapter adapter;
    SharedPreferences.Editor edit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_controler);

        wifiToolbar = (Toolbar)findViewById(R.id.toolbar_wifi);
        setSupportActionBar(wifiToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("와이파이 관리하기");
        m_WifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        mWifiMonitor = new WifiReceiver(this);
        IntentFilter receiverFilter = new IntentFilter();
        receiverFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        receiverFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mWifiMonitor, receiverFilter);

        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_add_wifi, null);

        final TextView text = (TextView) dialogView.findViewById(R.id.wifi);

        mWifiMonitor.setOnChangeNetworkStatusListener(new WifiReceiver.OnChangeNetworkStatusListener() {
            @Override
            public void OnChanged(int status, WifiInfo networkInfo) {
                switch (status){
                    case NETWORK_STATE_CONNECTED :
                        text.setText(networkInfo.getSSID());
                        break;
                }
            }
        });
        wifi = getSharedPreferences("wifi", MODE_PRIVATE);
        edit = wifi.edit();
        final ListView listView = (ListView) findViewById(R.id.wifilist);
        adapter = new WifiControlerListAdapter();
        for(int k = 1; k<=wifi.getInt("size", 0); k++){
            if(wifi.getString("wifi_SSID_"+k," ") != " ")
                adapter.addItem(wifi.getString("wifi_SSID_"+k,""), wifi.getString("wifi_name_"+k,""));
        }
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                adapter.deleteItem(pos);
                adapter.notifyDataSetChanged();

                TextView textView = (TextView) view.findViewById(R.id.ssid);

                for(int i = 1 ;i<=wifi.getInt("size", 0); i++){
                    if(wifi.getString("wifi_SSID_"+i,"") == textView.getText()) {
                        edit.remove("wifi_SSID_" + i);
                        edit.remove("wifi_name_"+i);
                        edit.apply();
                        break;
                    }
                }
            }
        });
        final AlertDialog.Builder builder = new AlertDialog.Builder(WifiControl.this);
        builder.setView(dialogView);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = (EditText) dialogView.findViewById(R.id.name);
                name = editText.getText().toString();

                Log.d("shared ",""+wifi.getInt("size", 1));
                Log.d("shared ",""+wifi.getString("wifi_SSID_"+asdf, "1"));
                Log.d("shared ",""+wifi.getString("wifi_name_"+asdf, "1"));

                asdf = wifi.getInt("size", 0);

                adapter.addItem(text.getText().toString(),name);
                adapter.notifyDataSetChanged();

                asdf++;

                edit.putString("wifi_SSID_"+asdf ,text.getText().toString());
                edit.putString("wifi_name_"+asdf ,name);
                edit.putInt("size", asdf);

                edit.apply();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_wifi);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(m_WifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED && text.getText() != null)
                    dialog.show();
                else
                    Toast.makeText(WifiControl.this, "연결된 와이파이가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        Toast.makeText(this, String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
        if(item.getItemId() == android.R.id.home){
            unregisterReceiver(mWifiMonitor);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
