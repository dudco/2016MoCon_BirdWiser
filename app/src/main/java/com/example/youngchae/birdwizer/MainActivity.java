package com.example.youngchae.birdwizer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    private  static final int FLOATING_CONTROL_REQUEST_CODE = 1;
    private  static final int WIFI_CONTROL_REQUEST_CODE = FLOATING_CONTROL_REQUEST_CODE+1;
    private  static final int ADD_CALENDAR_REQUEST_CODE = WIFI_CONTROL_REQUEST_CODE+1;
    public static List<MainRecyclerItem> items = new ArrayList<>();
    int todosize;
    MainRecyclerAdapter adapter;
    RecyclerView recyclerView;
    TextView textView;
    WifiReceiver mWifiMonitor;
    SharedPreferences todoList;
    SharedPreferences.Editor todoListEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences first = getSharedPreferences("first", MODE_PRIVATE);
        SharedPreferences.Editor firstEdit = first.edit();

        if(first.getBoolean("first", true)){
            startActivity(new Intent(MainActivity.this, TutorialActivity.class));
            firstEdit.putBoolean("first", false);
            firstEdit.apply();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        todoList = getSharedPreferences("todoList",MODE_PRIVATE);
        todoListEditor = todoList.edit();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add_Calendar.class);
                startActivityForResult(intent, ADD_CALENDAR_REQUEST_CODE);
            }
        });
        startService(new Intent(MainActivity.this, WIFICheckService.class));
//        mWifiMonitor = new WifiReceiver(this);
//        IntentFilter receiverFilter = new IntentFilter();
//        receiverFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        receiverFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        registerReceiver(mWifiMonitor, receiverFilter);

        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setExpandedTitleColor(Color.parseColor("#00000000"));
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_wifi :
                        startActivityForResult(new Intent(MainActivity.this, WifiControl.class), WIFI_CONTROL_REQUEST_CODE);
                        break;
                    case R.id.nav_floating :
                        startActivityForResult(new Intent(MainActivity.this, FloatingControl.class), FLOATING_CONTROL_REQUEST_CODE);
                        break;
                    case R.id.nav_devInfo :
                        break;
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                return true;
            }
        });

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NanumGothicBold.ttf");
        textView = (TextView) findViewById(R.id.todo_text);
        textView.setTypeface(tf);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
//        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int totalWidth = parent.getWidth();
//                int maxCardWidth = view.getWidth();
//                int sidePadding = (totalWidth - maxCardWidth) / 2;
//                sidePadding = Math.max(0, sidePadding);
//                outRect.set(sidePadding, 0, sidePadding,16);
//            }
//        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        for(int i = 1; i<=todoList.getInt("size", 0); i++){
            if(todoList.getString("todoList_title_"+i," ") != " " && todoList.getString("todoList_desc_"+i," ") != " " && todoList.getString("todoList_location_"+i," ") != " "){
                items.add(new MainRecyclerItem(
                        todoList.getInt("todoList_date_"+i, 0),
                        todoList.getString("todoList_title_"+i, " "),
                        todoList.getString("todoList_location_"+i, " ")
                ));
            }
        }

        adapter = new MainRecyclerAdapter(items);

        todosize = items.size();
        textView.setText("해야 할 일 : "+todosize);

        recyclerView.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback itemtouchhelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final MainRecyclerItem corrent_item = items.get(viewHolder.getAdapterPosition());
                final int pos = viewHolder.getAdapterPosition();
                items.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                TextView textView_location = (TextView) viewHolder.itemView.findViewById(R.id.location);
                SharedPreferences sharedPreferences = getSharedPreferences("wifi",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                for(int i = 1 ;i<=todoList.getInt("size", 0); i++){
                    if(todoList.getString("todoList_location_"+i,"") == textView_location.getText()) {
                        todoListEditor.remove("todoList_title_" + i);
                        todoListEditor.remove("todoList_desc_"+i);
                        todoListEditor.remove("todoList_location_"+i);
                        todoListEditor.remove("todoList_date_"+i);
                        todoListEditor.apply();
                        break;
                    }
//                    if(sharedPreferences.getString("wifi_name_"+i," ").equals(textView_location.getText())){
//                        editor.remove("wifi_SSID_"+i);
//                        editor.remove("wifi_name_"+i);
//                    }
                }

                todosize = items.size();
                textView.setText("해야 할 일 : "+todosize);
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemtouchhelper);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case WIFI_CONTROL_REQUEST_CODE :
                break;
            case FLOATING_CONTROL_REQUEST_CODE :
                break;
            case ADD_CALENDAR_REQUEST_CODE :
                if(resultCode == RESULT_OK){
                    String title = data.getStringExtra("title");
                    String desc = data.getStringExtra("desc");
                    String location = data.getStringExtra("location");
                    int date = data.getIntExtra("date", 0);

                    MainRecyclerItem item = new MainRecyclerItem(date, title, location);
                    items.add(item);
//                    adapter.notifyItemInserted(items.size()-1);
                    adapter.notifyDataSetChanged();
                    Log.d("data ",title+desc+location+date);

                    todosize = items.size();
                    Log.d("size ",""+todoList.getInt("size",0));
                    textView.setText("해야 할 일 : "+todosize);

                    int asdf = todoList.getInt("size", 1);

                    todoListEditor.putInt("size", asdf+1);
                    todoListEditor.putString("todoList_title_"+ asdf, title);
                    todoListEditor.putString("todoList_desc_"+ asdf, desc);
                    todoListEditor.putString("todoList_location_"+ asdf, location);
                    todoListEditor.putInt("todoList_date_"+ asdf, date);
                    todoListEditor.putBoolean("status",false);
                    todoListEditor.apply();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
