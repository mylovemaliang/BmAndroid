package cn.fuyoushuo.fqbb.view.Layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.jakewharton.rxbinding.view.RxView;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;
import rx.functions.Action1;

/**
 * Created by QA on 2016/7/19.
 */
public class SearchTypeMenu extends PopupWindow {

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

    //半透明背景色
    View belowGroudView;

    OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public SearchTypeMenu(Context context, View belowView) {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.context = new WeakReference<Context>(context);
        this.belowView = belowView;
        layoutInflater = LayoutInflater.from(context);
    }

    public SearchTypeMenu init() {
        if(context.get() == null || ((Activity)context.get()).isFinishing()){
            return this;
        }
        contentView = layoutInflater.inflate(R.layout.search_type_menu_area, null);
        recyclerView = (RecyclerView) contentView.findViewById(R.id.searchTypeMenu);
        belowGroudView = contentView.findViewById(R.id.below_backGroup);
        SearchMenuAdapter searchMenuAdapter = new SearchMenuAdapter();
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
        adapter.setData(SearchMenuAdapter.SearchRowItems);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.setContentView(contentView);
        this.setOutsideTouchable(false);
        RxView.clicks(belowGroudView).throttleFirst(1000, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                dismissWindow();
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
        this.showAsDropDown(belowView);
    }

    //关掉window
    public void dismissWindow() {
        if (context.get() == null) {
            return;
        }
        this.dismiss();
    }



    //由外部实现
    public interface OnItemClick{
        void onclick(View view,SearchMenuAdapter.RowItem rowItem);
    }

    @Override
    public String toString() {
        return "SearchTypeMenu{}";
    }
}
