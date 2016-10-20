package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.commonlib.utils.SeartchPo;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.presenter.impl.SearchPresenter;
import cn.fuyoushuo.fqbb.view.Layout.DividerItemDecoration;
import cn.fuyoushuo.fqbb.view.Layout.ItemDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.Layout.SearchTypeMenu;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.adapter.SearchLeftRviewAdapter;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;
import cn.fuyoushuo.fqbb.view.adapter.TbGoodDataAdapter;
import cn.fuyoushuo.fqbb.view.view.SearchView;
import rx.functions.Action1;

/**
 *  SearchFlagment
 */
public class SearchFlagment extends BaseFragment{

    public static String TAG_NAME = "search_flagment";

    private SearchPresenter searchPresenter;

    @Bind(R.id.search_flagment_toolbar)
    RelativeLayout toolbar;

    @Bind(R.id.serach_flagment_searchText)
    TextView searchText;

    @Bind(R.id.search_flagment_searchtype_btn)
    TextView SearchTypeButton;

    @Bind(R.id.search_flagment_cancel_area)
    View cancelView;

    @Bind(R.id.line1)
    View line1;

    //搜索菜单处理
    SearchTypeMenu searchTypeMenu;

    private static final String ARG_PARAM1 = "q";

    private String searchCateString;

    private LayoutInflater layoutInflater;

    //搜索词
    private String q = "";

    @Override
    protected int getRootLayoutId() {
        return R.layout.flagment_search_result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {
        if (getArguments() != null) {
            q = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchPresenter.onDestroy();
        destoryPopupWindow();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        RxView.clicks(cancelView).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                    RxBus.getInstance().send(new toMainFlagmentEvent());
            }
        });

        RxView.clicks(SearchTypeButton).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if(searchTypeMenu != null){
                   searchTypeMenu.showWindow();
                }
            }
        });

        RxView.clicks(searchText).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                SeartchPo seartchPo = new SeartchPo();
                seartchPo.setQ(q);
                RxBus.getInstance().send(new toSearchPromptFragmentEvent(seartchPo));
            }
        });
    }

    @Override
    public void initView(){

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFlagment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFlagment newInstance(String q) {
        SearchFlagment fragment = new SearchFlagment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,q);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param alpha
     */
    public void backgroundAlpha(float alpha) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha; //0.0-1.0
        window.setAttributes(lp);
    }

    //--------------------------------组装搜索条件------------------------------------------------------

    //刷新flagment 数据
   public void refreshSearchData(SeartchPo po){
       String q = po.getQ();
       this.q = q;
   }

    /**
     * 释放相关 popupwindow 的资源
     */
    private void destoryPopupWindow() {
        if(searchTypeMenu != null){
            searchTypeMenu.dismissWindow();
            searchTypeMenu = null;
        }
    }

    //------------------------------与SearcjActivity 通信-----------------------------------------------

    public class toSearchPromptFragmentEvent extends RxBus.BusEvent{

        private  SeartchPo seartchPo;

        public toSearchPromptFragmentEvent(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }

        public SeartchPo getSeartchPo() {
            return seartchPo;
        }

        public void setSeartchPo(SeartchPo seartchPo) {
            this.seartchPo = seartchPo;
        }
    }

    public class toMainFlagmentEvent extends RxBus.BusEvent{}

    public class toGoodInfoEvent extends RxBus.BusEvent{

        private String goodUrl;

        public toGoodInfoEvent(String goodUrl) {
            this.goodUrl = goodUrl;
        }

        public String getGoodUrl() {
            return goodUrl;
        }

        public void setGoodUrl(String goodUrl) {
            this.goodUrl = goodUrl;
        }
    }

    //-----------------------------------统计------------------------------------------------

}
