package cn.fuyoushuo.fqbb.view.Layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by QA on 2016/7/14.
 */
public class GoodItemsDecoration extends RecyclerView.ItemDecoration {

    private int leftSpace;

    private int rightSpace;

    public GoodItemsDecoration(int leftSpace, int rightSpace) {
        this.leftSpace = leftSpace;
        this.rightSpace = rightSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int childLayoutPosition = parent.getChildLayoutPosition(view);
        if (childLayoutPosition-1 %2==0) {
            outRect.left = leftSpace;
            outRect.right = rightSpace;
        }
    }
}
