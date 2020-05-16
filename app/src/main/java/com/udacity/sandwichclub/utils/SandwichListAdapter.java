package com.udacity.sandwichclub.utils;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.udacity.sandwichclub.R;

public class SandwichListAdapter extends BaseAdapter {
    private static final String TAG = "SandwichListAdapter";

    private String[] sandwiches;
    private static Context mContext;
    private int[] colors;

    public SandwichListAdapter(Context context, String[] sandwiches){
        this.sandwiches = sandwiches;
        mContext = context;
        colors = context.getResources().getIntArray(R.array.colors_array);
    }

    @Override
    public int getCount() {
        return sandwiches.length;
    }

    @Override
    public Object getItem(int i) {
        return sandwiches[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_sandwich, viewGroup,false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //set the data
        viewHolder.listIndex.setText(String.valueOf(i+1));
        viewHolder.sandwich.setText(sandwiches[i]);
        viewHolder.indexBackground.setColor(colors[i%colors.length]);

        return view;
    }

    private static class ViewHolder {
        private TextView sandwich, listIndex;
        private GradientDrawable indexBackground;

        private ViewHolder(View view) {
            sandwich = view.findViewById(R.id.tv_sandwich_item);
            listIndex = view.findViewById(R.id.tv_item_index);
            //index round background
            LayerDrawable layerDrawable = (LayerDrawable) listIndex.getBackground();
            indexBackground = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.bg_index_round);
        }
    }
}
