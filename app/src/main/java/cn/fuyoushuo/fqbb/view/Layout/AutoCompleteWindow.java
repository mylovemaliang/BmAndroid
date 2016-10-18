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

import java.lang.ref.WeakReference;
import java.util.List;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.adapter.AutoCompleteSearchItemAdapter;

/**
 * Created by QA on 2016/7/21.
 */
public class AutoCompleteWindow extends PopupWindow{

    //用来加载页面
    LayoutInflater layoutInflater;

    //popupwindow 所依附的组件
    View belowView;

    //内容VIEW
    View contentView;

    //获取上下文资料
    WeakReference<Context> context;

    RecyclerView hisRview;

    AutoCompleteSearchItemAdapter hisAdapter;

    RecyclerView hotRview;

    AutoCompleteSearchItemAdapter  hotAdapter;

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public AutoCompleteWindow(Context context, View belowView) {
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        this.context = new WeakReference<Context>(context);
        this.belowView = belowView;
        layoutInflater = LayoutInflater.from(context);
    }

    public AutoCompleteWindow init(){
        if(context.get() == null || ((Activity)context.get()).isFinishing()){
            return this;
        }
        contentView = layoutInflater.inflate(R.layout.auto_complete_search_view,null);
        hisRview = (RecyclerView) contentView.findViewById(R.id.auto_complete_his_rview);
        hotRview = (RecyclerView) contentView.findViewById(R.id.auto_complete_hot_rview);
        //定义hisrview
        hisRview.setHasFixedSize(true);
        hisAdapter = new AutoCompleteSearchItemAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context.get());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hisRview.setLayoutManager(linearLayoutManager);
        hisAdapter.setOnItemClickListener(new AutoCompleteSearchItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String item) {
                   onItemClick.onHisItemClick(view,item);
            }
        });
        hisRview.setAdapter(hisAdapter);

        //定义hotrview
        hotRview.setHasFixedSize(true);
        hotAdapter = new AutoCompleteSearchItemAdapter();
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(context.get());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotRview.setLayoutManager(linearLayoutManager2);
        hotAdapter.setOnItemClickListener(new AutoCompleteSearchItemAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, String item) {
                    onItemClick.onHotItemClick(view,item);
            }
        });
        hotRview.setAdapter(hotAdapter);
        //设置窗口的属性
        ColorDrawable backgroundColor = new ColorDrawable(context.get().getResources().getColor(R.color.transparent));
        this.setBackgroundDrawable(backgroundColor);
        this.setContentView(contentView);
        this.setOutsideTouchable(true);
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        //再设置模式，和Activity的一样，覆盖。
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //防止虚拟软键盘被弹出菜单遮住
        return this;
    }

    //展示window
    public void showWindow(){
        //判断弱引用上下文是否存在
        if(belowView == null || context.get() == null || ((Activity)context.get()).isFinishing()){
            return;
        }
        if (this.isShowing()){
            this.dismiss();
        }
        this.showAsDropDown(belowView);
    }

    //关掉window
    public void dismissWindow() {
        //判断弱引用上下文是否存在
        if(context.get() == null){
            return;
        }
        this.dismiss();
    }

    //更新历史数据
    public void updateHisRviewData(List<String> data){
        //判断弱引用上下文是否存在
        if(context.get() == null || ((Activity)context.get()).isFinishing()){
            return;
        }
        this.hisAdapter.setData(data);
        hisAdapter.notifyDataSetChanged();
    }

    //更新热门数据
    public void updateHotRviewData(List<String> data){
        //判断弱引用上下文是否存在
        if(context.get() == null || ((Activity)context.get()).isFinishing()){
            return;
        }
        this.hotAdapter.setData(data);
        hotAdapter.notifyDataSetChanged();
    }

    public interface OnItemClick{

        void onHisItemClick(View view,String item);

        void onHotItemClick(View view,String item);

    }

    @Override
    public String toString() {
        return "AutoCompleteWindow{}";
    }
}
