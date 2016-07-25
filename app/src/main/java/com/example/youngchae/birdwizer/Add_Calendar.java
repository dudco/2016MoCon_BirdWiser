package com.example.youngchae.birdwizer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by youngchae on 2016-07-16.
 */
public class Add_Calendar extends AppCompatActivity implements View.OnClickListener{
    EditText title;
    EditText desc;
    String location;

    int selected_text;
    public ArrayAdapter<String> spinnerAdapter;
    ArrayList<String> names = new ArrayList<String>();

    ImageView[] image = new ImageView[4];
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_calendar);

        WifiControl wifiControl = new WifiControl();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("일정 추가하기");
        sharedPreferences = getSharedPreferences("wifi", Activity.MODE_PRIVATE);
        for(int asdf = 1; asdf<=sharedPreferences.getInt("size", 0); asdf++){
            if(sharedPreferences.getString("wifi_name_"+asdf," ") != " ")
                names.add(sharedPreferences.getString("wifi_name_"+asdf, " "));
        }
        spinnerAdapter = new ArrayAdapter<String>(Add_Calendar.this, android.R.layout.simple_spinner_dropdown_item, names);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positon, long id) {
                location = adapterView.getItemAtPosition(positon).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        image[0] = (ImageView) findViewById(R.id.daybreak_image);
        image[1] = (ImageView) findViewById(R.id.day_image);
        image[2] = (ImageView) findViewById(R.id.evening_image);
        image[3] = (ImageView) findViewById(R.id.night_image);
        for(int i= 0;i<4;i++){
            image[i].setOnClickListener(this);
        }

        title = (EditText) findViewById(R.id.title_add);
        desc = (EditText) findViewById(R.id.desc_add);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home :
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.done:
                Intent intent = new Intent();
                intent.putExtra("title", title.getText().toString());
                intent.putExtra("desc", desc.getText().toString());
                intent.putExtra("location", location);
                intent.putExtra("date", selected_text);
                Log.d("shared ", sharedPreferences.getString("wifi_SSID_1",""));
                Log.d("shared ", sharedPreferences.getString("wifi_name_1",""));
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_calendar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        int currentClickImage = 0;
        int[] asdf = new int[4];

        asdf[0] = R.drawable.daybreak;
        asdf[1] = R.drawable.day;
        asdf[2] = R.drawable.evening;
        asdf[3] = R.drawable.night;

        switch (view.getId()){
            case R.id.daybreak_image :
                image[0].setImageResource(R.drawable.daybreak_click);
                selected_text = R.drawable.daybreak;
                currentClickImage = 0;
                break;
            case R.id.day_image :
                image[1].setImageResource(R.drawable.day_click);
                selected_text = R.drawable.day;
                currentClickImage = 1;
                break;
            case R.id.evening_image :
                image[2].setImageResource(R.drawable.evening_click);
                selected_text = R.drawable.evening;
                currentClickImage = 2;

                break;
            case R.id.night_image :
                image[3].setImageResource(R.drawable.night_click);
                selected_text = R.drawable.night;
                currentClickImage = 3;
                break;
        }
        for(int i = 0 ;i<4 ;i++){
            if(i == currentClickImage) continue;
            else image[i].setImageResource(asdf[i]);
        }
        Log.d("clicked ", String.valueOf(currentClickImage));
    }
}
