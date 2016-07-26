package com.example.youngchae.birdwizer;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
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
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.youngchae.birdwizer.Floating.FloatingControl;
import com.example.youngchae.birdwizer.Tutorial.TutorialActivity;
import com.example.youngchae.birdwizer.WIFI.WIFICheckService;
import com.example.youngchae.birdwizer.WIFI.WifiControl;
import com.example.youngchae.birdwizer.WIFI.WifiReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    CollapsingToolbarLayout collapsingToolbarLayout;
    private  static final int FLOATING_CONTROL_REQUEST_CODE = 1;
    private  static final int WIFI_CONTROL_REQUEST_CODE = FLOATING_CONTROL_REQUEST_CODE+1;
    private  static final int ADD_CALENDAR_REQUEST_CODE = WIFI_CONTROL_REQUEST_CODE+1;

    static final int REQUEST_ACCOUNT_PICKER = 10;
    static final int REQUEST_AUTHORIZATION = 11;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 12;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 13;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    public static List<MainRecyclerItem> items = new ArrayList<>();
    int todosize;
    MainRecyclerAdapter adapter;
    RecyclerView recyclerView;
    TextView textView;
    WifiReceiver mWifiMonitor;
    SharedPreferences todoList;
    SharedPreferences.Editor todoListEditor;

    protected Calendar mService;
    GoogleAccountCredential mCredential;

    int asdf = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences first = getSharedPreferences("first", MODE_PRIVATE);
        SharedPreferences.Editor firstEdit = first.edit();

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
        mService = new Calendar.Builder( AndroidHttp.newCompatibleTransport(), JacksonFactory.getDefaultInstance(), mCredential).setApplicationName("Google Calendar Study").build();

        if(first.getBoolean("first", true)) {
            startActivityForResult(new Intent(MainActivity.this, TutorialActivity.class), 213);
                firstEdit.putBoolean("first", false);
                firstEdit.apply();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        todoList = getSharedPreferences("todoList",MODE_PRIVATE);
        todoListEditor = todoList.edit();
        asdf = todoList.getInt("size",0);
        Log.e("size",asdf+"");

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
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        final int twidth = dm.widthPixels;
        final int cardWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350.0f, getApplicationContext().getResources().getDisplayMetrics());
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int totalWidth = twidth;
                int maxCardWidth = cardWidth;
                int sidePadding = (totalWidth - maxCardWidth) / 2;
                sidePadding = Math.max(0, sidePadding);
                outRect.set(sidePadding, 0, sidePadding,16);
            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        for(int i = 1; i <= todoList.getInt("size", 0); i++){
            if(!todoList.getString("todoList_title_"+i," ") .equals(" ") && !todoList.getString("todoList_desc_"+i," ").equals(" ") && !todoList.getString("todoList_location_"+i," ").equals(" ")){
                items.add(new MainRecyclerItem(
                        todoList.getInt("todoList_date_"+i, 0),
                        todoList.getString("todoList_title_"+i, " "),
                        todoList.getString("todoList_location_"+i, " ")
                ));
                Log.e("size",todoList.getInt("size", 0)+"");
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

    private void getResultsFromApi(){
        if(! isGooglePalyServicesAvailable()){
            acquireGooglePlayServices();
        }else if(mCredential.getSelectedAccountName() == null){
            chooseAccount();
        }else if(!isDeviceOnline()){
//            mOutputText.setText("No network connection available.");
        }
//        else{
//            eventModel.removeAll(eventModel);
//            AsyncLoad.run(GoogleLink.this);
//        }
    }
    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount(){
        if(EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)){
            String accountName = getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
            if(accountName != null){
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            }else{
                startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            }
        }else{
            EasyPermissions.requestPermissions(this, "This app needs to access your Google account (via Contacts).", REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    private boolean isDeviceOnline(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePalyServicesAvailable(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int  connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(apiAvailability.isUserResolvableError(connectionStatusCode)){
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectioniStatusCode){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this, connectioniStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();

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

                    asdf++;

                    todoListEditor.putInt("size", asdf);
                    todoListEditor.putString("todoList_title_"+ asdf, title);
                    todoListEditor.putString("todoList_desc_"+ asdf, desc);
                    todoListEditor.putString("todoList_location_"+ asdf, location);
                    todoListEditor.putInt("todoList_date_"+ asdf, date);
                    todoListEditor.putBoolean("status",false);
                    todoListEditor.apply();
                }
                Log.e("size",todoList.getInt("size", 0)+"");
                break;
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if(resultCode != RESULT_OK){
//                    mOutputText.setText("This app requires Google Play Services. Please install " +
//                            "Google Play Services on your device and relaunch this app.");
                }else{
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if(resultCode == RESULT_OK && data != null && data.getExtras()!=null){
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(accountName!=null){
                        SharedPreferences setting  = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = setting.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }break;
            case REQUEST_AUTHORIZATION:
                if(resultCode == RESULT_OK){
                    getResultsFromApi();
                }
                break;
            case 213:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Utils.canDrawOverlays(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    }
                }
                break;
            case 1234:
                getResultsFromApi();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
