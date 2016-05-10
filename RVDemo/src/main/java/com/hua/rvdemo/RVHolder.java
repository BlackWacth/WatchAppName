package com.hua.rvdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by ZHONG WEI  HUA on 2016/5/10.
 */
public class RVHolder extends RecyclerView.ViewHolder {
    LinearLayout container;
    TextView text;

    public RVHolder(View v) {
        super(v);
//      container = (LinearLayout) v;
        container = (LinearLayout) v.findViewById(R.id.item_content);
        text = (TextView) v.findViewById(R.id.item_text);
    }
}
