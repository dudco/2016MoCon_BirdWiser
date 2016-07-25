package com.example.youngchae.birdwizer.Floating;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.youngchae.birdwizer.R;

public class MyService extends Service {
    private float initialTouchX;
    private float initialTouchY;
    private int initialX;
    private int initialY;
    private long lastTouchDown;
    private int TOUCH_TIME_THRESHOLD = 150;
    WindowManager windowManager;
    private int width;
    WindowManager.LayoutParams param;
    View rootview;
    LinearLayout bubbleView;
    private MoveAnimator animator;
    WindowManager.LayoutParams params;
    LinearLayout bubbleContain;
    boolean isShow = false;
    String intentName = null;
    TextView title;
    TextView desc;
    TextView location;
    ImageView date;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("start","command");
        intentName = intent.getStringExtra("SSID");
        Log.d("intentName",intentName+" ");
        if(intentName != null){
            SharedPreferences sharedPreferences = getSharedPreferences("todoList", MODE_PRIVATE);
            for(int i = 1 ; i<=sharedPreferences.getInt("size",0); i++){
                Log.d("shared",sharedPreferences.getString("todoList_location_"+i," "));
                if(sharedPreferences.getString("todoList_location_"+i," ").equals(intentName)){
                    title.setText(sharedPreferences.getString("todoList_title_"+i," "));
                    location.setText(sharedPreferences.getString("todoList_location_"+i," "));
                    desc.setText(sharedPreferences.getString("todoList_desc_"+i," "));
                    date.setImageResource(sharedPreferences.getInt("todoList_date_"+i,0));
                }
            }
        }else{
            location.setText(intentName);
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //"MainActivity.class" 대신에, 상단바 알림창 눌렀을 때 "시작할 액티비티.class" 넣기
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, FloatingControl.class), PendingIntent.FLAG_UPDATE_CURRENT
        );

        //icon_splash 대신 상단바 알림창 아이콘 설정
        Notification notification = new Notification.Builder(this)
//                .setSmallIcon(R.drawable.icon_splash)
                .setTicker("잠금 서비스가 실행되었습니다.")
                .setContentTitle("일해라! 개발자의 화면 잠금이 실행중입니다.")
                .setContentText("터치해서 설정합니다.")
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

        bubbleView  = (LinearLayout) inflater.inflate(R.layout.bubble, null);
        final ImageView bubbleImage = (ImageView) bubbleView.findViewById(R.id.image_bubble);
        animator = new MoveAnimator();
        rootview = bubbleView;
        //bubbleview
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSPARENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;
        windowManager.addView(bubbleView, params);

        bubbleContain = (LinearLayout) inflater.inflate(R.layout.fragment_page, null);
        title = (TextView) bubbleContain.findViewById(R.id.title_page);
        desc = (TextView) bubbleContain.findViewById(R.id.desc);
        location= (TextView) bubbleContain.findViewById(R.id.location_page);
        date = (ImageView) bubbleContain.findViewById(R.id.date_page);

        Log.d("container","find");

        bubbleContain.setVisibility(View.GONE);
        bubbleContain.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        params.x =0;
        params.y = 200;
        windowManager.addView(bubbleContain, params);
        bubbleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                param= (WindowManager.LayoutParams) view.getLayoutParams();
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
//                        Log.d("touch","down");
                        initialX = param.x;
                        initialY = param.y;
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        playAnimationClickDown();
                        lastTouchDown = System.currentTimeMillis();
                        updateSize();
                        animator.stop();
                        break;
                    case MotionEvent.ACTION_MOVE:
//                        Log.d("touch","move");
                        if(isShow){
                            bubbleContain.setVisibility(View.GONE);
                            isShow = false;
                        }
                        int x = initialX + (int)(motionEvent.getRawX() - initialTouchX);
                        int y = initialY + (int)(motionEvent.getRawY() - initialTouchY);
                        param.x = x;
                        param.y = y;
                        windowManager.updateViewLayout(view, param);
//                        if (getLayoutCoordinator() != null) {
//                            getLayoutCoordinator().notifyBubblePositionChanged(this, x, y);
//                        }
                        break;
                    case MotionEvent.ACTION_UP:
//                        Log.d("touch","up");
//                        Log.d("goToWall","start");
                        goToWall();
//                        if (getLayoutCoordinator() != null) {
//                            getLayoutCoordinator().notifyBubbleRelease(this);
                        playAnimationClickUp();
//                        }
                        if (System.currentTimeMillis() - lastTouchDown < TOUCH_TIME_THRESHOLD) {
                            Log.d("touch", "!!");
//                            if (onBubbleClickListener != null) {
//                                onBubbleClickListener.onBubbleClick(this);
                            animator.start(0,0);
//                            }
                            if(!isShow){
//                                bubbleContain.setX(0);
//                                bubbleContain.setY(bubbleImage.getHeight());
                                bubbleContain.setVisibility(View.VISIBLE);
                                Log.d("x", bubbleContain.getX()+"");
                                Log.d("y", bubbleContain.getY()+"");
                                Log.d("height", bubbleContain.getHeight()+"");


                                isShow = true;
                            }else{
                                bubbleContain.setVisibility(View.GONE);
                                isShow = false;
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }
    private void updateSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Display display = windowManager.getDefaultDisplay(); //@@@@@@@@@@@@@@@@@@@@@@
        Point size = new Point();
        display.getSize(size);
        width = (size.x - rootview.getWidth());
    }
    public void goToWall() {
        int middle = width / 2;
        float nearestXWall = params.x >= middle ? width : 0;
//        Log.d("animator","start");
        animator.start(nearestXWall, params.y);
    }
    public void move(float deltaX, float deltaY) {
        params.x += deltaX;
        params.y += deltaY;
        Log.d("move","start");
        windowManager.updateViewLayout(bubbleView, params);
    }
    private void playAnimationClickDown() {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getApplicationContext(), R.animator.bubble_down_click_animator);
            animator.setTarget(bubbleView);
            animator.start();
    }

    private void playAnimationClickUp() {
            AnimatorSet animator = (AnimatorSet) AnimatorInflater
                    .loadAnimator(getApplicationContext(), R.animator.bubble_up_click_animator);
            animator.setTarget(bubbleView);
            animator.start();
    }
    private class MoveAnimator implements Runnable {
        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        private void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
//            Log.d("run","start");
//            Log.d("rootView", rootview.getRootView().getParent()+"");
            if (rootview.getRootView() != null && rootview.getRootView().getParent() != null) {
                float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
                float deltaX = (destinationX -  params.x) * progress;
                float deltaY = (destinationY -  params.y) * progress;
                move(deltaX, deltaY);
                if (progress < 1) {
                    handler.post(this);
                }
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManager.removeView(bubbleContain);
        windowManager.removeView(bubbleView);
    }
}
