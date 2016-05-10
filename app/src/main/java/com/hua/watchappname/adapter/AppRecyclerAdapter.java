package com.hua.watchappname.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.watchappname.R;
import com.hua.watchappname.entity.App;
import com.hua.watchappname.utils.KeywordUtils;
import com.hua.watchappname.utils.L;

import java.util.ArrayList;
import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.AppHolder> implements Filterable{

    private List<App> mList, originalApps;
    private Context mContext;
    private Filter mFilter;
    private int mColor;

    public AppRecyclerAdapter(Context context, List<App> list) {
        mContext = context;
        originalApps = list;
        mList = new ArrayList<>(list);
        mColor = mContext.getResources().getColor(R.color.colorAccent);
        setHasStableIds(true);
    }

    @Override
    public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
        return new AppHolder(view);
    }

    @Override
    public void onBindViewHolder(AppHolder holder, int position) {
        App app = mList.get(position);
        holder.icon.setImageDrawable(app.getIcon());
        String name = KeywordUtils.matcherSearchText(mColor, app.getAppName(), "视频").toString();
        String pck = KeywordUtils.matcherSearchText(mColor, app.getPckName(), "android").toString();
        holder.appName.setText(name);
        holder.pckName.setText(pck);
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void notifyAll(List<App> apps) {
        mList.clear();
        mList.addAll(apps);
        notifyDataSetChanged();
    }

    public void animationTo(List<App> apps) {
        applyAndAnimateRemovals(apps);
//        applyAndAnimateAdditions(apps);
//        applyAndAnimateMovedItem(apps);
    }

    private void applyAndAnimateRemovals(List<App> newApps) {
        for(int i = mList.size()-1; i >= 0; i--){
            final App app = mList.get(i);
            if(!newApps.contains(app)) {
//                L.i("index = " + i);
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<App> newApps) {
//        L.i("add app = " + newApps.size());
        for(int i = 0; i < newApps.size(); i++) {
            final App app = newApps.get(i);
            if(!mList.contains(app)) {
                addItem(i, app);
            }
        }
    }

    private void applyAndAnimateMovedItem(List<App> newApps) {
        for(int topPosition = newApps.size() - 1; topPosition >= 0; topPosition --) {
            final App app = newApps.get(topPosition);
            final int fromPosition = mList.indexOf(app);
            if(fromPosition >= 0 && fromPosition != topPosition) {
                moveItem(fromPosition, topPosition);
            }
        }
    }

    private App moveItem(int fromPosition, int topPosition) {
        final App app = mList.remove(fromPosition);
        mList.add(topPosition, app);
        notifyItemMoved(fromPosition, topPosition);
        return app;
    }

    private void addItem(int position, App app) {
//        L.i("add ===> " + app.toString());
        mList.add(position, app);
        notifyItemInserted(position);
    }

    private App removeItem(int position) {
//        final App app = mList.remove(position);
        L.i("------------> " + position);
//        notifyDataSetChanged();

        notifyItemRemoved(1);  //报下标越界,IndexOutOfBoundsException
        return null;
    }

    @Override
    public Filter getFilter() {
        return mFilter == null ? mFilter = new SeacherFilter() : mFilter ;
    }

    class SeacherFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if(results.values == null) {
                mList.clear();
                mList.addAll(originalApps);
            }

            if(constraint == null || constraint.length() == 0) {
                mList.clear();
                mList.addAll(originalApps);
            }else{
                List<App> list = filter(mList, constraint.toString());
                results.values = list;
                results.count = list.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.values == null) {
                return ;
            }
            animationTo((List<App>) results.values);
        }

        private List<App> filter(List<App> apps, String query) {
            query = query.toLowerCase();
            final List<App> filterApps = new ArrayList<>();
            int color = mContext.getResources().getColor(R.color.colorAccent);
            for(App app : apps){
                final String appName = app.getAppName().toLowerCase();
                final String pckName = app.getPckName().toLowerCase();
                if(appName.contains(query) || pckName.contains(query)){
                    String name = KeywordUtils.matcherSearchText(color, app.getAppName(), query).toString();
                    String pck = KeywordUtils.matcherSearchText(color, app.getPckName(), query).toString();
                    L.i("name = " + name);
                    L.i("pck = " + pck);
                    app.setAppName(name);
                    app.setPckName(pck);
                    filterApps.add(app);
                }
            }
            return filterApps;
        }
    }

    class AppHolder extends RecyclerView.ViewHolder{

        ImageView icon;
        TextView appName;
        TextView pckName;

        public AppHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.item_app_icon);
            appName = (TextView) itemView.findViewById(R.id.item_app_name);
            pckName = (TextView) itemView.findViewById(R.id.item_app_package_name);
        }
    }
}
