package com.hua.rvdemo;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DefaultItemAnimator mDefaultItemAnimator = new DefaultItemAnimator();
    ArgbEvaluator mColorEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//        mRecyclerView.setLayoutManager(new MLinearLayoutManager(this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setItemAnimator(mDefaultItemAnimator);
        mRecyclerView.setItemAnimator(new MItemAnimator());
        mRecyclerView.setAdapter(new RVAdapter());

    }

    private class RVAdapter extends RecyclerView.Adapter<RVHolder> {

        ArrayList<Integer> mColors = new ArrayList<>();

        public RVAdapter() {
            generateData();
        }

        @Override
        public RVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_rv, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    deleteItem(v);
                    changeItem(v);
                }
            });
            return new RVHolder(view);
        }

        @Override
        public void onBindViewHolder(final RVHolder holder, final int position) {
            int color = mColors.get(position);
            holder.container.setBackgroundColor(color);
            holder.text.setText("#" + Integer.toHexString(color));
//            holder.container.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    deleteItem(holder.getLayoutPosition());
//                    deleteItem(holder.getAdapterPosition());
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mColors.size();
        }

        /**
         * 删除
         * @param view
         */
        private void deleteItem(View view) {
            int positon = mRecyclerView.getChildAdapterPosition(view);
            Log.i("hzw", "position = " + positon);
            if(positon != RecyclerView.NO_POSITION) {
                mColors.remove(positon);
                notifyItemRemoved(positon);
            }
        }

        private void deleteItem(int positon) {
            Log.i("hzw", "position = " + positon);
            if(positon != RecyclerView.NO_POSITION) {
                mColors.remove(positon);
                notifyItemRemoved(positon);
            }
        }

        /**
         * 添加
         * @param view
         */
        private void addItem(View view) {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if(position != RecyclerView.NO_POSITION) {
                mColors.add(position, generateColor());
                notifyItemInserted(position);
            }
        }

        /**
         * 修改
         * @param view
         */
        private void changeItem(View view) {
            int position = mRecyclerView.getChildAdapterPosition(view);
            if (position != RecyclerView.NO_POSITION) {
                int color = generateColor();
                mColors.set(position, color);
                notifyItemChanged(position);
            }
        }

        private int generateColor() {
            int red = ((int) (Math.random() * 200));
            int green = ((int) (Math.random() * 200));
            int blue = ((int) (Math.random() * 200));
            return Color.rgb(red, green, blue);
        }

        private void generateData() {
            for (int i = 0; i < 10; i++) {
                mColors.add(generateColor());
            }
        }
    }
}
