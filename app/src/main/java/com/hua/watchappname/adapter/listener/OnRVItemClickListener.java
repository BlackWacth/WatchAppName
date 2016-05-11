package com.hua.watchappname.adapter.listener;

import android.view.View;

/**
 * RV item单击监听器
 */
public interface OnRVItemClickListener<T> {

    void onItemClick(View v, int position, T t);

}
