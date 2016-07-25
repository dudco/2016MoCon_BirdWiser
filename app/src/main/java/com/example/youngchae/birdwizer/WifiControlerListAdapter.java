package com.example.youngchae.birdwizer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by youngchae on 2016-07-16.
 */
public class WifiControlerListAdapter extends BaseAdapter {
    private ArrayList<WifiControlerListItem> items = new ArrayList<>();


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final int position = i;
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.wifi_listview, viewGroup, false);
        }

        TextView ssid = (TextView) view.findViewById(R.id.ssid);
        TextView wifiLocation = (TextView) view.findViewById(R.id.location_wifi);
        ImageView imageView = (ImageView) view.findViewById(R.id.delete_wifi);

        WifiControlerListItem wifiControlerListItem = items.get(i);

        ssid.setText(wifiControlerListItem.getWifiSSID());
        wifiLocation.setText(wifiControlerListItem.getLocation());


        return view;
    }

    public void addItem(String ssid, String location){
        WifiControlerListItem wifiControlerListItem = new WifiControlerListItem();
        wifiControlerListItem.setWifiSSID(ssid);
        wifiControlerListItem.setLocation(location);

        items.add(wifiControlerListItem);
    }
    public void deleteItem(int pos){
        items.remove(pos);
    }
}
