package cn.fuyoushuo.fqbb.view.Layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by QA on 2016/7/14.
 */
public class GoodTipDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            outRect.left = 5;
            outRect.right =5;
            outRect.bottom = 10;
            outRect.top = 10;
    }
}
