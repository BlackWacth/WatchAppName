package com.hua.watchappname;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hua.watchappname.adapter.AppRecyclerAdapter;
import com.hua.watchappname.adapter.listener.OnRVItemClickListener;
import com.hua.watchappname.adapter.listener.OnRVItemLongClickListener;
import com.hua.watchappname.entity.App;
import com.hua.watchappname.global.C;
import com.hua.watchappname.loader.AppListLoader;
import com.hua.watchappname.utils.KeywordUtils;
import com.hua.watchappname.utils.L;

import java.util.ArrayList;
import java.util.List;


public class AppListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<App>>{

    private static final String APP_TYPE = "APP_TYPE";

    private int mAppType;

    private RecyclerView mRecyclerView;
    private AppRecyclerAdapter mAdapter;
    private List<App> mList;
    private SearchView mSearchView;
    private int mColor;
    private ClipboardManager mClipboardManager;
    private ContentLoadingProgressBar mProgressBar;

    public AppListFragment() {

    }

    public static AppListFragment newInstance(int appType) {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        args.putInt(APP_TYPE, appType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAppType = getArguments().getInt(APP_TYPE, C.USER_APP);
        }
        mColor = getResources().getColor(R.color.colorAccent);
        mClipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);
        mProgressBar = (ContentLoadingProgressBar) view.findViewById(R.id.app_progress_bar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mList == null) {
            mList = new ArrayList<>();
        }
        mAdapter = new AppRecyclerAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        getLoaderManager().initLoader(0x1111, null, this);

        initEvent();
        return view;
    }

    private void copy(String text){
        ClipData data = ClipData.newPlainText(text, text);
        mClipboardManager.setPrimaryClip(data);
        showSnackbar("复制成功");
    }

    private void showSnackbar(String text) {
        Snackbar.make(getView(), text, Snackbar.LENGTH_SHORT).show();
    }

    private void openAppInfo(String pckName) {
        Uri uri = Uri.parse("package:" + pckName);
        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri));
    }

    private void openApp(String pckName){
        try{
            Intent intent = getActivity().getPackageManager().getLaunchIntentForPackage(pckName);
            startActivity(intent);
        }catch (Exception e) {
            showSnackbar("无法打开");
        }
    }

    private void uninstallApp(String pckName) {
        Uri uri = Uri.parse("package:" + pckName);
        startActivity(new Intent(Intent.ACTION_DELETE, uri));
    }

    private void initEvent() {
        mAdapter.addItemClickListener(new OnRVItemClickListener<App>() {
            @Override
            public void onItemClick(final View v, int position, final App app) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(app.getAppName().toString());
                builder.setMessage(app.getPckName().toString());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setNegativeButton("详情", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openAppInfo(app.getPckName().toString());
                    }
                });
                builder.setNeutralButton("复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        copy(app.getAppName().toString() + " " + app.getPckName().toString());
                    }
                });

                builder.show();
            }
        });

        mAdapter.addItemLongClickListener(new OnRVItemLongClickListener<App>() {
            @Override
            public void onItemLongClick(View v, int position, final App app) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(app.getAppName().toString());
                builder.setMessage(app.getPckName().toString());
                builder.setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openApp(app.getPckName().toString());
                    }
                });

                if(mAppType == C.USER_APP) {
                    builder.setNeutralButton("卸载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uninstallApp(app.getPckName().toString());
                        }
                    });
                }
                builder.show();
            }
        });
    }

    @Override
    public Loader<List<App>> onCreateLoader(int id, Bundle args) {
        mProgressBar.show();
        return new AppListLoader(getActivity(), mAppType);
    }

    @Override
    public void onLoadFinished(Loader<List<App>> loader, List<App> data) {
        mProgressBar.hide();
        mList.clear();
        mList.addAll(data);
        mAdapter.notifyAll(data);
    }

    @Override
    public void onLoaderReset(Loader<List<App>> loader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_search_menu, menu);
        final MenuItem item = menu.findItem(R.id.search_view);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        initSearchView(mSearchView);
    }

    private void initSearchView(SearchView searchView) {
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 输入完成，提交时触发。
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            /**
             * 在输入时触发。
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                doSearch(newText);
//                mAdapter.getFilter().filter(newText);
//                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
    }

    private List<App> filter(List<App> apps, String query) {
        query = query.toLowerCase();
        final List<App> filterApps = new ArrayList<>();
        for(App app : apps){
            final String appName = app.getAppName().toString().toLowerCase();
            final String pckName = app.getPckName().toString().toLowerCase();
            if(appName.contains(query) || pckName.contains(query)){
                app.setAppName(KeywordUtils.matcherSearchText(mColor, app.getAppName().toString(), query));
                app.setPckName(KeywordUtils.matcherSearchText(mColor, app.getPckName().toString(), query));
                filterApps.add(app);
            }
        }
        return filterApps;
    }

    private void doSearch(String searchText) {
        final List<App> filterApps = filter(mList, searchText);
        mAdapter.animationTo(filterApps);
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
