package me.ajax.cardswipe1;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aj on 2018/4/23
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Integer> drawableResList = new ArrayList<>();


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setDrawableRes(drawableResList.get(position));
    }

    @Override
    public int getItemCount() {
        return drawableResList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }

        void setDrawableRes(int drawableRes) {
            imageView.setImageResource(drawableRes);
        }
    }

    public void setDrawableResList(List<Integer> drawableResList) {
        this.drawableResList = drawableResList;
    }
}

