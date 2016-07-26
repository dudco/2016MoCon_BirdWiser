package com.example.youngchae.birdwizer.Tutorial;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.youngchae.birdwizer.R;

public class CustomAdapter extends PagerAdapter {

    LayoutInflater inflater;

    public CustomAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 6;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        // TODO Auto-generated method stub

        View view=null;

        view= inflater.inflate(R.layout.viewpager_childview, null);

        ImageView img= (ImageView)view.findViewById(R.id.img_viewpager_childimage);

        switch (position){
            case 0:img.setImageResource(R.drawable.tutorial1); break;
            case 1:img.setImageResource(R.drawable.tutorial2); break;
            case 2:img.setImageResource(R.drawable.tutorial3); break;
            case 3:img.setImageResource(R.drawable.tutorial4); break;
            case 4:img.setImageResource(R.drawable.tutorial5); break;
            case 5: TutorialActivity.tutorial.finish();
        }

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub

        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View v, Object obj) {
        // TODO Auto-generated method stub
        return v==obj;
    }

}
