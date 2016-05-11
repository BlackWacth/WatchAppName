package com.hua.watchappname.loader;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.content.AsyncTaskLoader;
import android.text.SpannableString;

import com.hua.watchappname.entity.App;
import com.hua.watchappname.global.C;
import com.hua.watchappname.receiver.PackageIntentReceiver;
import com.hua.watchappname.utils.L;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppListLoader extends AsyncTaskLoader<List<App>>{

    final PackageManager mPackageManager;

    private List<App> mApps;
    private PackageIntentReceiver mReceiver;
    private int mType;

    public AppListLoader(Context context, int type) {
        super(context);
        this.mType = type;
        mPackageManager = context.getPackageManager();
    }

    @Override
    public List<App> loadInBackground() {
        List<PackageInfo> packages = mPackageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_DISABLED_COMPONENTS);

        List<App> apps = new ArrayList<>();
        App app = null;
        for(PackageInfo info : packages) {
            app = new App();
            app.setPckName(new SpannableString(info.packageName));
            app.setVersion(info.versionName);
            ApplicationInfo appInfo = info.applicationInfo;
            app.setAppName(new SpannableString(appInfo.loadLabel(mPackageManager).toString()));
            app.setIcon(appInfo.loadIcon(mPackageManager));
            if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                app.setType(C.USER_APP);
            }else {
                app.setType(C.SYSTEM_APP);
            }
            apps.add(app);
        }

        Collections.sort(apps, new Comparator<App>() {
            Collator mCollator = Collator.getInstance();
            @Override
            public int compare(App lhs, App rhs) {
                return mCollator.compare(lhs.getAppName().toString(), rhs.getAppName().toString());
            }
        });
        return apps;
    }

    @Override
    public void deliverResult(List<App> apps) {
        if(isReset()) {
            if(apps != null){
                apps.clear();
                apps = null;
            }
        }
        List<App> newApp = new ArrayList<>();
        if(mType == C.USER_APP) {
            for(App app : apps) {
                if(app.getType() == C.USER_APP) {
                    newApp.add(app);
                }
            }
        }else {
            for(App app : apps) {
                if(app.getType() == C.SYSTEM_APP) {
                    newApp.add(app);
                }
            }
        }

        if(isStarted()) {
            super.deliverResult(newApp);
        }
    }

    @Override
    protected void onStartLoading() {
        if(mApps != null) {
            deliverResult(mApps);
        }

        if(mReceiver == null) {
            mReceiver = new PackageIntentReceiver(this);
        }

        if(takeContentChanged() || mApps == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<App> data) {
        super.onCanceled(data);
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if(mApps != null) {
            mApps.clear();
            mApps = null;
        }

        if(mReceiver != null) {
            getContext().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
}
