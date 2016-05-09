package com.hua.watchappname;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.hua.watchappname.adapter.AppRecyclerAdapter;
import com.hua.watchappname.entity.App;
import com.hua.watchappname.global.C;
import com.hua.watchappname.loader.AppListLoader;
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
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(mList == null) {
            mList = new ArrayList<>();
        }
        mAdapter = new AppRecyclerAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        getLoaderManager().initLoader(0x1111, null, this);
        return view;
    }

    @Override
    public Loader<List<App>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getActivity(), mAppType);
    }

    @Override
    public void onLoadFinished(Loader<List<App>> loader, List<App> data) {
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
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            /**
             * 输入完成，提交时触发。
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * 在输入时触发。
             * @param newText
             * @return
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                L.i("query = " + newText);
//                doSearch(newText);
                mAdapter.getFilter().filter(newText);
                mRecyclerView.scrollToPosition(0);
                return true;
            }
        });
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                return false;
//            }
//        });
    }

    private List<App> filter(List<App> apps, String query) {
        query = query.toLowerCase();
        final List<App> filterApps = new ArrayList<>();
        for(App app : apps){
            final String appName = app.getAppName().toLowerCase();
            final String pckName = app.getPckName().toLowerCase();
            if(appName.contains(query) || pckName.contains(query)){
                filterApps.add(app);
            }
        }
        return filterApps;
    }

    private void doSearch(String searchText) {
        L.i("all = " + mList.size());
        final List<App> filterApps = filter(mList, searchText);
        L.i("filter = " + filterApps.size());
        mAdapter.animationTo(filterApps);
        mRecyclerView.scrollToPosition(0);
    }
}
