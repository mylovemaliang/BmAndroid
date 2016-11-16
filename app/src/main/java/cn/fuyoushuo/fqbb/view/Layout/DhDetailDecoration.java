package cn.fuyoushuo.fqbb.view.Layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by QA on 2016/7/14.
 */
public class DhDetailDecoration extends RecyclerView.ItemDecoration {


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            outRect.left = 10;
            outRect.right =10;
            outRect.bottom = 8;
            outRect.top = 8;
    }
}
