package com.hua.watchappname.adapter.listener;

import android.view.View;

/**
 * Created by ZHONG WEI  HUA on 2016/5/11.
 */
public interface OnRVItemLongClickListener<T> {

    void onItemLongClick(View v, int position, T t);

}
