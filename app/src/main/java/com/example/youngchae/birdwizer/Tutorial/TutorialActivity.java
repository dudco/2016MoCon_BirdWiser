package com.example.youngchae.birdwizer.Tutorial;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.example.youngchae.birdwizer.R;

public class TutorialActivity extends Activity {

    ViewPager pager;
    public static Activity tutorial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        tutorial = TutorialActivity.this;
        pager = (ViewPager) findViewById(R.id.pager);

        CustomAdapter adapter = new CustomAdapter(getLayoutInflater());

        pager.setAdapter(adapter);

    }

}