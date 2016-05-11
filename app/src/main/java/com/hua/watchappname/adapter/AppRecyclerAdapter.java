package com.hua.watchappname.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.watchappname.R;
import com.hua.watchappname.adapter.listener.OnRVItemClickListener;
import com.hua.watchappname.adapter.listener.OnRVItemLongClickListener;
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

    private OnRVItemClickListener<App> mItemClickListener;
    private OnRVItemLongClickListener<App> mItemLongClickListener;

    public AppRecyclerAdapter(Context context, List<App> list) {
        mContext = context;
        originalApps = list;
        mList = new ArrayList<>(list);
        mColor = mContext.getResources().getColor(R.color.colorAccent);
//        setHasStableIds(true);
    }

    @Override
    public AppHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app, parent, false);
        return new AppHolder(view);
    }

    @Override
    public void onBindViewHolder(final AppHolder holder, int position) {
        final App app = mList.get(position);
        holder.icon.setImageDrawable(app.getIcon());
        holder.appName.setText(app.getAppName());
        holder.pckName.setText(app.getPckName());
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, holder.getAdapterPosition(), app);
                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(v, holder.getAdapterPosition(), app);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public OnRVItemClickListener<App> getItemClickListener() {
        return mItemClickListener;
    }

    public void addItemClickListener(OnRVItemClickListener<App> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public OnRVItemLongClickListener<App> getItemLongClickListener() {
        return mItemLongClickListener;
    }

    public void addItemLongClickListener(OnRVItemLongClickListener<App> itemLongClickListener) {
        mItemLongClickListener = itemLongClickListener;
    }

    //    @Override
//    public long getItemId(int position) {
//        return position;
//    }

    public void notifyAll(List<App> apps) {
        mList.clear();
        mList.addAll(apps);
        notifyDataSetChanged();
    }

    public void animationTo(List<App> apps) {
        applyAndAnimateRemovals(apps);
        applyAndAnimateAdditions(apps);
//        applyAndAnimateMovedItem(apps);
    }

    private void applyAndAnimateRemovals(List<App> newApps) {
        for(int i = mList.size()-1; i >= 0; i--){
            final App app = mList.get(i);
            if(!newApps.contains(app)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<App> newApps) {
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
        mList.add(position, app);
        notifyItemInserted(position);
    }

    private App removeItem(int position) {
        final App app = mList.remove(position);
        notifyItemRemoved(position);
        return app;
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
            for(App app : apps){
                final String appName = app.getAppName().toString().toLowerCase();
                final String pckName = app.getPckName().toString().toLowerCase();
                if(appName.contains(query) || pckName.contains(query)){
                    SpannableString name = KeywordUtils.matcherSearchText(mColor, app.getAppName().toString(), query);
                    SpannableString pName = KeywordUtils.matcherSearchText(mColor, app.getPckName().toString(), query);
                    L.i("app : " + name);
                    L.i("pck : " + pName);

                    app.setAppName(name);
                    app.setPckName(pName);
                    filterApps.add(app);
                }
            }
            return filterApps;
        }
    }

    class AppHolder extends RecyclerView.ViewHolder{

        CardView container;
        ImageView icon;
        TextView appName;
        TextView pckName;

        public AppHolder(View itemView) {
            super(itemView);
            container = (CardView) itemView.findViewById(R.id.item_card_view);
            icon = (ImageView) itemView.findViewById(R.id.item_app_icon);
            appName = (TextView) itemView.findViewById(R.id.item_app_name);
            pckName = (TextView) itemView.findViewById(R.id.item_app_package_name);
        }
    }
}
