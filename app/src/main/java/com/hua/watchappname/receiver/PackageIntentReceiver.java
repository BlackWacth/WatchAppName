package com.hua.watchappname.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.hua.watchappname.loader.AppListLoader;

public class PackageIntentReceiver extends BroadcastReceiver{

    final AppListLoader mLoader;

    public PackageIntentReceiver(AppListLoader loader) {
        mLoader = loader;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        mLoader.getContext().registerReceiver(this, filter);

        IntentFilter sdFilter = new IntentFilter();
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        sdFilter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        mLoader.getContext().registerReceiver(this, sdFilter);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
    }
}

