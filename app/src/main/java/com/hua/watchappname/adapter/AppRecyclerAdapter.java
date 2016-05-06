package com.hua.watchappname.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.watchappname.R;
import com.hua.watchappname.entity.App;
import com.hua.watchappname.utils.L;

import java.util.List;

public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.AppHolder>{

    private List<App> mList;
    private Context mContext;

    public AppRecyclerAdapter(Context context, List<App> list) {
        mContext = context;
        mList = list;
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
        holder.appName.setText(app.getAppName());
        holder.pckName.setText(app.getPckName());
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
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
