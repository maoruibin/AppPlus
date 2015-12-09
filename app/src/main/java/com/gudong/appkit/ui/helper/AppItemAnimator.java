package com.gudong.appkit.ui.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.gudong.appkit.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GuDong on 12/9/15 17:07.
 * Contact with 1252768410@qq.com.
 */
// TODO remove and add anim has some problem
public class AppItemAnimator extends SimpleItemAnimator {
    private static final int KEY_DURATION_TIME = 500;
    List<RecyclerView.ViewHolder>mAnimationAddViewHolders = new ArrayList<>();
    List<RecyclerView.ViewHolder>mAnimationRemoveViewHolders = new ArrayList<>();

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        TextView tv = (TextView) holder.itemView.findViewById(android.R.id.text1);
        Logger.i("animateRemove "+tv.getText().toString());
//        return mAnimationRemoveViewHolders.add(holder);
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        TextView tv = (TextView) holder.itemView.findViewById(android.R.id.text1);
        Logger.i("animateAdd "+tv.getText().toString());
        mAnimationAddViewHolders.add(holder);
        return true;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return false;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromLeft, int fromTop, int toLeft, int toTop) {
        return false;
    }

    @Override
    public void runPendingAnimations() {
        if(!mAnimationAddViewHolders.isEmpty()){
            AnimatorSet animator;
            View target;
            for(int i=0;i<mAnimationAddViewHolders.size();i++){
                final RecyclerView.ViewHolder viewHolder = mAnimationAddViewHolders.get(i);
                target = viewHolder.itemView;
                animator = new AnimatorSet();
                animator.playTogether(
                        ObjectAnimator.ofFloat(target,"translationX",-target.getMeasuredWidth(),0,0f),
                        ObjectAnimator.ofFloat(target,"alpha",0.5f,1.0f)
                );
                animator.setTarget(target);
                animator.setDuration(KEY_DURATION_TIME);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAnimationAddViewHolders.remove(viewHolder);
                        if(!isRunning()){
                            dispatchAnimationsFinished();
                        }
                    }
                });
                //animator.setStartDelay(i*300);
//                TextView tv = (TextView) viewHolder.itemView.findViewById(android.R.id.text1);
//                Logger.i("setStartDelay "+tv.getText().toString() +" "+i*300 +"毫秒 ");

                animator.start();
            }
        }

        if(!mAnimationRemoveViewHolders.isEmpty()){
            Logger.i("remove size "+mAnimationRemoveViewHolders.size());
            AnimatorSet animator;
            View target;
            for(final RecyclerView.ViewHolder viewHolder:mAnimationRemoveViewHolders){
                target = viewHolder.itemView;
                animator = new AnimatorSet();
                animator.playTogether(
                        ObjectAnimator.ofFloat(target,"translationX",0,0f,-target.getMeasuredWidth()),
                        ObjectAnimator.ofFloat(target,"alpha",0.0f,1.0f)
                );
                animator.setTarget(target);
                animator.setDuration(KEY_DURATION_TIME);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mAnimationRemoveViewHolders.remove(viewHolder);
                        if(!isRunning()){
                            dispatchAnimationsFinished();
                        }
                    }
                });
                animator.start();
            }
        }
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return !(mAnimationAddViewHolders.isEmpty() && mAnimationRemoveViewHolders.isEmpty());
    }
}
