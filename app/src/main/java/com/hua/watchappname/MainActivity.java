package com.hua.watchappname;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.hua.watchappname.adapter.ViewPagerAdapter;
import com.hua.watchappname.global.C;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    private List<ViewPagerAdapter.FragmentModel> mList;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.main_view_apger);
        mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        mTabLayout = (TabLayout) findViewById(R.id.main_tab_layout);

        setSupportActionBar(mToolbar);

        initRecyclerView();
    }

    private void initRecyclerView() {
        if(mList == null) {
            mList = new ArrayList<>();
            mList.add(new ViewPagerAdapter.FragmentModel(AppListFragment.newInstance(C.USER_APP), "用户应用"));
            mList.add(new ViewPagerAdapter.FragmentModel(AppListFragment.newInstance(C.SYSTEM_APP), "系统应用"));
        }

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mList);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
