package com.hua.rvdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZHONG WEI  HUA on 2016/5/10.
 */
public class MItemAnimator extends DefaultItemAnimator {

    private ArgbEvaluator mColorEvaluator = new ArgbEvaluator();

    private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator();
    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator();

    private ArrayMap<RecyclerView.ViewHolder, AnimatorInfo> mAnimatorMap = new ArrayMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        // This allows our custom change animation on the contents of the holder instead
        // of the default behavior of replacing the viewHolder entirely
        return true;
    }

    @NonNull
    private ItemHolderInfo getItemHolderInfo(RVHolder viewHolder, ColorTextInfo info) {
        final RVHolder myHolder = viewHolder;
        final int bgColor = ((ColorDrawable) myHolder.container.getBackground()).getColor();
        info.color = bgColor;
        info.text = (String) myHolder.text.getText();
        return info;
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new ColorTextInfo();
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder) {
        ColorTextInfo info = (ColorTextInfo) super.recordPostLayoutInformation(state, viewHolder);
        return getItemHolderInfo((RVHolder) viewHolder, info);
    }


    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        ColorTextInfo info = (ColorTextInfo) super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
        return getItemHolderInfo((RVHolder) viewHolder, info);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull final RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {

        if(oldHolder != newHolder) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        final RVHolder viewHolder = (RVHolder) newHolder;
        ColorTextInfo oldInfo = (ColorTextInfo) preInfo;
        ColorTextInfo newInfo = (ColorTextInfo) postInfo;
        int oldColor = oldInfo.color;
        int newColor = newInfo.color;
        final String oldText = oldInfo.text;
        final String newText = newInfo.text;

        LinearLayout newContainer = viewHolder.container;
        final TextView newTextView = viewHolder.text;

        AnimatorInfo runningInfo = mAnimatorMap.get(newHolder);
        long preAnimPlayTime = 0;
        boolean firstHalf = false;
        if(runningInfo != null) {
            firstHalf = runningInfo.oldTextRotator != null && runningInfo.oldTextRotator.isRunning();
            preAnimPlayTime = firstHalf ? runningInfo.oldTextRotator.getCurrentPlayTime() : runningInfo.newTextRotator.getCurrentPlayTime();
            runningInfo.overalAnim.cancel();
        }

        ObjectAnimator fadeToBlack = null;
        final ObjectAnimator fadeFromBlack;
        if(runningInfo == null || firstHalf) {
            int startColor = oldColor;
            if(runningInfo != null) {
                startColor = (int) runningInfo.fadeToBlackAnim.getAnimatedValue();
            }
            fadeToBlack = ObjectAnimator.ofInt(newContainer, "backgroundColor", startColor, Color.BLACK);
            fadeToBlack.setEvaluator(mColorEvaluator);
            if(runningInfo != null) {
                fadeToBlack.setCurrentPlayTime(preAnimPlayTime);
            }
        }

        fadeFromBlack = ObjectAnimator.ofInt(newContainer, "backgroundColor", Color.BLACK, newColor);
        fadeFromBlack.setEvaluator(mColorEvaluator);
        if(runningInfo != null && !firstHalf) {
            fadeFromBlack.setCurrentPlayTime(preAnimPlayTime);
        }

        AnimatorSet bgAnim = new AnimatorSet();
        if(fadeToBlack != null) {
            bgAnim.playSequentially(fadeToBlack, fadeFromBlack);
        }else {
            bgAnim.play(fadeFromBlack);
        }

        ObjectAnimator oldTextRotate = null, newTextRotate;
        if(runningInfo == null || firstHalf) {
            oldTextRotate = ObjectAnimator.ofFloat(newTextView, View.ROTATION_X, 0, 90);
            oldTextRotate.setInterpolator(mAccelerateInterpolator);
            if(runningInfo != null) {
                oldTextRotate.setCurrentPlayTime(preAnimPlayTime);
            }
            oldTextRotate.addListener(new AnimatorListenerAdapter() {
                boolean mCanceled = false;
                @Override
                public void onAnimationStart(Animator animation) {
                    newTextView.setText(oldText);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCanceled = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!mCanceled) {
                        newTextView.setText(newText);
                    }
                }
            });
        }

        newTextRotate = ObjectAnimator.ofFloat(newTextView, View.ROTATION_X, -90, 0);
        newTextRotate.setInterpolator(mDecelerateInterpolator);
        if(runningInfo != null && !firstHalf) {
            newTextRotate.setCurrentPlayTime(preAnimPlayTime);
        }
        AnimatorSet textAnim = new AnimatorSet();
        if(oldTextRotate != null) {
            textAnim.playSequentially(oldTextRotate, newTextRotate);
        }else{
            textAnim.play(newTextRotate);
        }

        AnimatorSet changeAnim = new AnimatorSet();
        changeAnim.playTogether(bgAnim, textAnim);
        changeAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(newHolder);
                mAnimatorMap.remove(newHolder);
            }
        });
        changeAnim.start();

        AnimatorInfo runingAnimInfo = new AnimatorInfo(changeAnim,fadeToBlack,fadeFromBlack, oldTextRotate, newTextRotate);
        mAnimatorMap.put(newHolder, runingAnimInfo);

        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        if(!mAnimatorMap.isEmpty()) {
            final int numRunning = mAnimatorMap.size();
            for(int i = numRunning; i >=0; i --) {
                if(item == mAnimatorMap.keyAt(i)) {
                    mAnimatorMap.valueAt(i).overalAnim.cancel();
                }
            }
        }
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() || !mAnimatorMap.isEmpty();
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
        if(!mAnimatorMap.isEmpty()) {
            final int numRunning = mAnimatorMap.size();
            for(int i = numRunning; i >=0; i --) {
                mAnimatorMap.valueAt(i).overalAnim.cancel();
            }
        }
    }

}

class AnimatorInfo {
    Animator overalAnim;
    ObjectAnimator fadeToBlackAnim, fadeFromBlackAnim, oldTextRotator, newTextRotator;

    public AnimatorInfo(Animator overalAnim, ObjectAnimator fadeToBlackAnim, ObjectAnimator fadeFromBlackAnim, ObjectAnimator oldTextRotator, ObjectAnimator newTextRotator) {
        this.overalAnim = overalAnim;
        this.fadeToBlackAnim = fadeToBlackAnim;
        this.fadeFromBlackAnim = fadeFromBlackAnim;
        this.oldTextRotator = oldTextRotator;
        this.newTextRotator = newTextRotator;
    }
}

class ColorTextInfo extends RecyclerView.ItemAnimator.ItemHolderInfo{
    int color;
    String text;
}