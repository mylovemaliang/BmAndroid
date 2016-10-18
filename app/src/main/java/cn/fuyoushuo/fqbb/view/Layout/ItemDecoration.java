package cn.fuyoushuo.fqbb.view.Layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.fuyoushuo.fqbb.R;

/**
 * @Package com.micky.commonlib.view
 * @Project CommonProj
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-01-04 23:13
 */
public class ItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public ItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.list_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

//            if(i == 0){
//                int firstTop = child.getTop() + params.bottomMargin;
//                int firstBottom =  firstTop + mDivider.getIntrinsicHeight();
//                mDivider.setBounds(left,firstTop,right,firstBottom);
//                mDivider.draw(c);
//            }

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}