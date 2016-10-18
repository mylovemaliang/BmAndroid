package cn.fuyoushuo.fqbb.view.Layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by QA on 2016/7/14.
 */
public class CateItemsDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //由于每行都只有3个，所以第一个都是3的倍数，把左边距设为0
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.left = 20;
            outRect.right =40;
        }else{
            outRect.left = 40;
            outRect.right = 40;
        }
    }
}
