package cn.fuyoushuo.fqbb.view.Layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import java.lang.ref.WeakReference;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;

/**
 * Created by QA on 2016/7/19.
 */
public class SearchPointsOrderMenu extends PopupWindow {

    //用来加载页面
    LayoutInflater layoutInflater;

    //popupwindow 所依附的组件
    View belowView;

    //内容所依附的view
    View contentView;

    //获取上下文资料
    WeakReference<Context> context;

    //滚动内容
    RecyclerView recyclerView;

    OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public SearchPointsOrderMenu(Context context, View belowView) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.context = new WeakReference<Context>(context);
        this.belowView = belowView;
        layoutInflater = LayoutInflater.from(context);
    }

    public SearchPointsOrderMenu init() {
        if(context.get() == null || ((Activity)context.get()).isFinishing()){
            return this;
        }
        contentView = layoutInflater.inflate(R.layout.search_type_menu_area, null);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.searchTypeMenu);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.get());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.addItemDecoration(new ItemDecoration(context));
        SearchMenuAdapter adapter = new SearchMenuAdapter();
        adapter.setOnRowClick(new SearchMenuAdapter.OnRowClick() {
            @Override
            public void onClick(View view, SearchMenuAdapter.RowItem rowItem) {
                onItemClick.onclick(view, rowItem);
            }
        });
        adapter.setData(SearchMenuAdapter.pointsOrderSearchItems);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.setContentView(contentView);
        this.setOutsideTouchable(true);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismissWindow();
                backgroundAlpha(1.0f);
            }
        });
        return this;
    }

    //展示window
    public void showWindow() {
        if(belowView == null || context.get() == null || ((Activity)context.get()).isFinishing()){
            return;
        }
        if (this.isShowing()) {
            this.dismiss();
        }
        this.setFocusable(true);
        ColorDrawable backgroundColor = new ColorDrawable(context.get().getResources().getColor(R.color.transparent));
        this.setBackgroundDrawable(backgroundColor);
        //防止虚拟软键盘被弹出菜单遮住
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        showAtLocation(belowView,Gravity.BOTTOM|Gravity.LEFT,0,0);
        backgroundAlpha(0.5f);

    }

    //关掉window
    public void dismissWindow() {
        if (context.get() == null) {
            return;
        }
        this.dismiss();
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param alpha
     */
    private void backgroundAlpha(float alpha) {
        if(context.get() != null){
          Window window = ((FragmentActivity)(context.get())).getWindow();
          WindowManager.LayoutParams lp = window.getAttributes();
          lp.alpha = alpha; //0.0-1.0
          window.setAttributes(lp);
    }
    }




    //由外部实现
    public interface OnItemClick{
        void onclick(View view, SearchMenuAdapter.RowItem rowItem);
    }

    @Override
    public String toString() {
        return "SearchTypeMenu{}";
    }
}
