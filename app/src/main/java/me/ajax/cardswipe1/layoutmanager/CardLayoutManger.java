package me.ajax.cardswipe1.layoutmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * Created by aj on 2018/4/25
 */

public class CardLayoutManger extends RecyclerView.LayoutManager {

    private int viewWidth;
    private int viewHeight;
    private SparseArray<Rect> mAllItemFrames = new SparseArray<>();
    private SparseIntArray mAllItemScales = new SparseIntArray();
    private SparseBooleanArray mHasAttachedItems = new SparseBooleanArray();

    private int mAllOffset = 0;
    private RecyclerView.Recycler recycler;

    private int topCardIndex = 0;


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    @Override
    public void onScrollStateChanged(int state) {
        if (state == RecyclerView.SCROLL_STATE_IDLE) {

            int to = getWidth();

            if (Math.abs(mAllOffset) < to / 5) {
                to = 0;
            }

            if (mAllOffset < 0) to *= -1;

            startScroll(mAllOffset, to);
        }
    }

    private void startScroll(int from, final int to) {

        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                mAllOffset = (int) animation.getAnimatedValue();

                //刷新位置
                refreshFrames();
                layoutItems(recycler);
            }
        });
        animator.setInterpolator(new AccelerateInterpolator());
        if (to != 0) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mAllOffset = 0;
                    topCardIndex = Math.min(topCardIndex + 1, getItemCount() - 1);
                }
            });
        }
        animator.start();
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        mAllOffset += -dx;

        //刷新位置
        refreshFrames();
        layoutItems(recycler);

        return dx;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getItemCount() == 0 || state.isPreLayout()) return;
        this.recycler = recycler;

        mAllOffset = 0;
        topCardIndex = 0;
        l("onLayoutChildren ");

        for (int i = 0; i < getItemCount(); i++) {
            mHasAttachedItems.put(i, false);
        }

        //测量
        View view = recycler.getViewForPosition(0);
        addView(view);
        measureChildWithMargins(view, 0, 0);
        viewWidth = view.getMeasuredWidth();
        viewHeight = view.getMeasuredHeight();

        detachAndScrapAttachedViews(recycler);

        //刷新位置
        refreshFrames();

        layoutItems(recycler);
    }

    private float slideFraction;

    private void refreshFrames() {

        int topOffset = (getHeight() - viewHeight) / 2;
        int leftOffset = (getWidth() - viewWidth) / 2;

        slideFraction = Math.abs(mAllOffset / (getWidth() / 3F));
        slideFraction = Math.min(slideFraction, 1);

        for (int i = topCardIndex; i < getItemCount(); i++) {

            //缩放
            if (i == topCardIndex) {
                mAllItemScales.put(i, 100);
            } else if (i < 3 + topCardIndex) {
                int baseScale = 100 - (i - topCardIndex) * 10;
                mAllItemScales.put(i, (int) (baseScale + 10 * slideFraction));
            } else {
                mAllItemScales.put(i, 80);
            }

            //基本偏移
            int offset;
            if (i == topCardIndex) {
                offset = 0;
            } else if (i < 3 + topCardIndex) {
                int baseOffset = (i - topCardIndex) * dp(30);
                offset = baseOffset - (int) (dp(30) * slideFraction);
            } else {
                offset = dp(60);
            }

            //更新位置信息
            Rect rect = mAllItemFrames.get(i);
            if (rect == null) rect = new Rect();
            rect.set(leftOffset, topOffset + offset, leftOffset + viewWidth,
                    topOffset + offset + viewHeight);
            mAllItemFrames.put(i, rect);
        }

        Rect rect = mAllItemFrames.get(topCardIndex);
        rect.left = leftOffset + mAllOffset;
        rect.right = leftOffset + viewWidth + mAllOffset;
    }

    private void layoutItems(RecyclerView.Recycler recycler) {

        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            int position = getPosition(view);
            if (position < topCardIndex) {
                removeAndRecycleView(view, recycler);
                mHasAttachedItems.put(position, false);
            } else {
                layoutItem(view, position);
                mHasAttachedItems.put(position, true);
            }
        }

        for (int i = 0; i < 4; i++) {

            int position = topCardIndex + i;
            if (position >= getItemCount()) continue;
            if (mHasAttachedItems.get(position)) continue;

            View view = recycler.getViewForPosition(position);
            addView(view);
            layoutItem(view, position);
        }
    }

    private void layoutItem(View view, int position) {
        measureChildWithMargins(view, 0, 0);
        Rect rect = mAllItemFrames.get(position);
        layoutDecorated(view, rect.left, rect.top, rect.right, rect.bottom);

        float scale = mAllItemScales.get(position) / 100F;
        view.setScaleX(scale);
        view.setScaleY(scale);


        //轻微的旋转
        if (position == topCardIndex) {
            if (mAllOffset > 0) {
                view.setRotation(10 * slideFraction);
            } else {
                view.setRotation(-10 * slideFraction);
            }
        } else {
            view.setRotation(0);
        }
    }

    private int dp(float dp) {
        return (int) dp * 3;
    }

    static void l(Object... list) {
        String text = "";
        for (Object o : list) {
            text += "   " + o.toString();
        }
        Log.e("######", text);
    }
}
