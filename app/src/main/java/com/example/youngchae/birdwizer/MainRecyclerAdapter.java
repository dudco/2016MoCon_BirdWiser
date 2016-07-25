package com.example.youngchae.birdwizer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by youngchae on 2016-07-17.
 */
public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MyViewHolder> {
    List<MainRecyclerItem> items;

    public MainRecyclerAdapter(List<MainRecyclerItem> items) {
        this.items = items;
    }

    @Override
    public MainRecyclerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_todo_recyclerview, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MainRecyclerAdapter.MyViewHolder holder, int position) {
        MainRecyclerItem item = items.get(position);
        holder.title.setText(item.getMainTitle());
        holder.location.setText(item.getLocation());
        holder.date.setImageResource(item.getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.linearLayout.setBackgroundResource(R.drawable.todo_completed);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView date;
        TextView title;
        TextView location;
        LinearLayout linearLayout;
        public MyViewHolder(View itemView) {
            super(itemView);
            date = (ImageView) itemView.findViewById(R.id.date);
            title = (TextView) itemView.findViewById(R.id.title);
            location = (TextView) itemView.findViewById(R.id.location);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.recyclerlinear);
        }
    }
}
