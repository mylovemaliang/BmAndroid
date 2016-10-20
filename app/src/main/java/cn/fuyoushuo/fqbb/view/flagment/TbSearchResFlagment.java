package cn.fuyoushuo.fqbb.view.flagment;

import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Map;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;

/**
 * Created by MALIANG on 2016/10/20.
 * 用于展现淘宝的搜索结果页
 */
public class TbSearchResFlagment extends BaseFragment{

    public static final String TAG_NAME = "tbSearchResFlagment";

    @Bind(R.id.search_flagment_left_btn)
    TextView searchleftBtn;

    @Bind(R.id.search_flagment_right_btn)
    TextView searchrightBtn;

    @Bind(R.id.line2)
    View line2;

    //用于呈现搜索浮层
    PopupWindow popupWindow;

    // 屏幕的width
    private int mScreenWidth;
    // 屏幕的height
    private int mScreenHeight;
    // PopupWindow的width
    private int mPopupWindowWidth;
    // PopupWindow的height
    private int mPopupWindowHeight;

    //左边搜索部分
    View leftSearchView;

    //右边搜索部分
    View rightSearchView;

    //右边搜索部分
    private Map<Integer,View> rightSearchViewMap = new ArrayMap<Integer, View>();

    //左边部分 recycleView
    RecyclerView searchLeftRview;

    //搜索结果呈现部分
    //@Bind(R.id.search_result_rview)
    RecyclerView searchResultRview;

    //@Bind(R.id.search_result_refreshLayout)
    RefreshLayout refreshLayout;

    @Bind(R.id.search_totop_area)
    View toTopView;

    @Bind(R.id.search_totop_icon)
    TextView toTopIcon;


    @Override
    protected int getRootLayoutId() {
        return R.layout.view_taobao_search;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }


}
