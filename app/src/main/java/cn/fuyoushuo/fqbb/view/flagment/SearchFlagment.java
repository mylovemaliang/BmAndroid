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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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
public class SearchFlagment extends BaseFragment implements SearchView{

    public static String TAG_NAME = "search_flagment";

    private SearchPresenter searchPresenter;

    @Bind(R.id.search_flagment_toolbar)
    RelativeLayout toolbar;

    @Bind(R.id.serach_flagment_searchText)
    TextView searchText;

    @Bind(R.id.search_flagment_searchtype_btn)
    TextView SearchTypeButton;

    @Bind(R.id.search_flagment_left_btn)
    TextView searchleftBtn;

    @Bind(R.id.search_flagment_right_btn)
    TextView searchrightBtn;

    @Bind(R.id.search_flagment_cancel_area)
    View cancelView;

    @Bind(R.id.line1)
    View line1;

    @Bind(R.id.line2)
    View line2;

    View leftBelowBackGroup;
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

    View rightSearchView;

    //搜索菜单处理
    SearchTypeMenu searchTypeMenu;

    //右边搜索部分
    private Map<Integer,View> rightSearchViewMap = new ArrayMap<Integer, View>();

    //左边部分 recycleView
    RecyclerView searchLeftRview;

    //搜索结果呈现部分
    @Bind(R.id.search_result_rview)
    RecyclerView searchResultRview;

    @Bind(R.id.search_result_refreshLayout)
    RefreshLayout refreshLayout;

    @Bind(R.id.search_totop_area)
    View toTopView;

    @Bind(R.id.search_totop_icon)
    TextView toTopIcon;

    //左边部分recycleview adapter
    SearchLeftRviewAdapter searchLeftRviewAdapter;

    //搜索商品结果列表 adapter
    TbGoodDataAdapter tbGoodDataAdapter;


    private static final String ARG_PARAM1 = "searchCateString";


    private String searchCateString;

    //搜索条件处理
    private SearchCondition searchCondition;

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
            searchCateString = getArguments().getString(ARG_PARAM1);
            searchCondition = SearchCondition.newInstance(searchCateString);
            searchCondition.updateSearchKeyValue("q",q);
        }
        searchPresenter = new SearchPresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchPresenter.onDestroy();
        destoryPopupWindow();
        if(!rightSearchViewMap.isEmpty()){
            rightSearchViewMap.clear();
        }
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchTypeMenu.setOnItemClick(new SearchTypeMenu.OnItemClick() {
            @Override
            public void onclick(View view, SearchMenuAdapter.RowItem rowItem) {
                if(searchTypeMenu != null){
                    searchTypeMenu.dismissWindow();
                }
                String sortCode = rowItem.getSortCode();
                if(SearchCondition.search_cate_superfan.equals(sortCode)){
                    changeSearchType(SearchCondition.search_cate_superfan);
                    searchPresenter.getSearchResult(searchCondition,false);
                }else if(SearchCondition.search_cate_commonfan.equals(sortCode)){
                    changeSearchType(SearchCondition.search_cate_commonfan);
                    searchPresenter.getSearchResult(searchCondition,false);
                }else if(SearchCondition.search_cate_taobao.equals(sortCode)){
                    changeSearchType(SearchCondition.search_cate_taobao);
                    searchPresenter.getSearchResult(searchCondition,false);
                }
                searchleftBtn.setText("综合排序");
                SearchTypeButton.setText(rowItem.getRowDesc());
            }});

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

        //
        RxView.clicks(searchleftBtn).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {

               @Override
               public void call(Void aVoid) {
                  if(popupWindow == null || mactivity == null || mactivity.isFinishing()){
                      return;
                  }
                  if(popupWindow.isShowing()){
                      popupWindow.dismiss();
                  }
                popupWindow.setFocusable(true);
                updatePopupWindow(leftSearchView);
                ColorDrawable backgroundColor = new ColorDrawable(getResources().getColor(R.color.transparent));
                popupWindow.setBackgroundDrawable(backgroundColor);
                //防止虚拟软键盘被弹出菜单遮住
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //backgroundAlpha(0.5f);
                if(line2 != null) {
                    int[] location = new int[2];
                    line2.getLocationInWindow(location);
                    int height = location[1];
                    popupWindow.showAtLocation(line2,Gravity.TOP,0,height+line2.getHeight());
                    backgroundAlpha(0.5f);
                }
            }
        });
        RxView.clicks(searchrightBtn).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {

            @Override
            public void call(Void aVoid) {
                if(popupWindow == null || mactivity == null || mactivity.isFinishing()){
                    return;
                }
                if(popupWindow.isShowing()){
                    popupWindow.dismiss();
                }
                popupWindow.setFocusable(true);
                updatePopupWindow(rightSearchView);
                ColorDrawable backgroundColor = new ColorDrawable(getResources().getColor(R.color.transparent));
                popupWindow.setBackgroundDrawable(backgroundColor);
                //防止虚拟软键盘被弹出菜单遮住
                popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                //backgroundAlpha(0.5f);
                if(line2 != null){
                    int[] location = new int[2];
                    line2.getLocationInWindow(location);
                    int height = location[1];
                    popupWindow.showAtLocation(line2,Gravity.TOP,0,height+line2.getHeight());
                    backgroundAlpha(0.5f);
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
                seartchPo.setSearchType(searchCondition.getCurrentSearchCate());
                RxBus.getInstance().send(new toSearchPromptFragmentEvent(seartchPo));
            }
        });

        //
        RxView.clicks(toTopView).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                searchResultRview.scrollToPosition(0);
            }
        });
    }

    @Override
    public void initView(){

        initIconFont();
        //数据初始化
        initPopupWindow();

        searchTypeMenu = new SearchTypeMenu(getActivity(),line1).init();

        changeSearchType(searchCondition.getCurrentSearchCate());
        SearchTypeButton.setText(SearchCondition.getSearchTypeDesc(searchCondition.getCurrentSearchCate()));
        searchText.setText(q);
        if(!TextUtils.isEmpty(q)) {
            searchPresenter.getSearchResult(searchCondition, false);
        }
        //-----------------------------------------初始化搜索结果列表---------------------------------------------------------------
        searchResultRview.setHasFixedSize(true);
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mactivity,2);
        gridLayoutManager.setSpeedFast();
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        searchResultRview.setLayoutManager(gridLayoutManager);
        tbGoodDataAdapter = new TbGoodDataAdapter();
        tbGoodDataAdapter.setOnLoad(new TbGoodDataAdapter.OnLoad() {
            @Override
            public void onLoadImage(SimpleDraweeView view, TaoBaoItemVo goodItem) {
                  int mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
                  int intHundred = CommonUtils.getIntHundred(mScreenWidth/2);
                  if(intHundred > 800){
                    intHundred = 800;
                  }
                  if(!BaseActivity.isTablet(mactivity)){
                      intHundred = 400;
                  }
                  String imgurl = goodItem.getPic_path();
                  imgurl = imgurl.replaceFirst("_[1-9][0-9]{0,2}x[1-9][0-9]{0,2}\\.jpg","");
                  imgurl = imgurl+ "_"+intHundred+"x"+intHundred+".jpg";
                  view.setAspectRatio(1.0F);
                  view.setImageURI(Uri.parse(imgurl));
            }

            @Override
            public void onItemClick(View view, TaoBaoItemVo goodItem) {
                  String goodUrl = goodItem.getUrl();
                  if(!TextUtils.isEmpty(goodUrl)) {
                      RxBus.getInstance().send(new toGoodInfoEvent(goodUrl));
                  }
            }

            @Override
            public void onFanliInfoLoaded(View fanliView, TaoBaoItemVo taoBaoItemVo) {
                  searchPresenter.getDiscountInfo(fanliView,taoBaoItemVo);
            }
        });

        searchResultRview.setAdapter(tbGoodDataAdapter);

        searchResultRview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                 if(gridLayoutManager.findFirstVisibleItemPosition() == 0){
                     toTopView.setVisibility(View.GONE);
                 }else{
                     toTopView.setVisibility(View.VISIBLE);
                 }
            }
        });

        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                String currentSearchCate = searchCondition.getCurrentSearchCate();
                if(SearchCondition.search_cate_superfan.equals(currentSearchCate)){
                    Integer toPage = (Integer) searchCondition.getSearchItems().get("toPage").getValues();
                    searchCondition.updateSearchKeyValue("toPage",toPage+1);
                }else if(SearchCondition.search_cate_commonfan.equals(currentSearchCate)){
                    Integer toPage = (Integer) searchCondition.getSearchItems().get("toPage").getValues();
                    searchCondition.updateSearchKeyValue("toPage",toPage+1);
                }else if(SearchCondition.search_cate_taobao.equals(currentSearchCate)){
                    Integer page = (Integer) searchCondition.getSearchItems().get("page").getValues();
                    searchCondition.updateSearchKeyValue("page",page+1);
                }
                searchPresenter.getSearchResult(searchCondition,true);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String currentSearchCate = searchCondition.getCurrentSearchCate();
                if(SearchCondition.search_cate_superfan.equals(currentSearchCate)){
                    searchCondition.updateSearchKeyValue("toPage",1);
                }else if(SearchCondition.search_cate_commonfan.equals(currentSearchCate)){
                    searchCondition.updateSearchKeyValue("toPage",1);
                }else if(SearchCondition.search_cate_taobao.equals(currentSearchCate)){
                    Integer page = (Integer) searchCondition.getSearchItems().get("page").getValues();
                    searchCondition.updateSearchKeyValue("page",1);
                }
                searchPresenter.getSearchResult(searchCondition,false);
                refreshLayout.setRefreshing(false);
                return;
            }
        });
    }



//    private void showPopupMenu(final View view) {
//        // View当前PopupMenu显示的相对View的位置
//        final PopupMenu popupMenu = new PopupMenu(getActivity(),view);
//        // menu布局
//        popupMenu.getMenuInflater().inflate(R.menu.search_btn_menu, popupMenu.getMenu());
//        // menu的item点击事件
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int itemId = item.getItemId();
//                switch (itemId){
//                    case R.id.searchmenu_action_chaojifan :
//                        changeSearchType(SearchCondition.search_cate_superfan);
//                        searchPresenter.getSearchResult(searchCondition,false);
//                        break;
//                    case R.id.searchmenu_action_fanli :
//                        changeSearchType(SearchCondition.search_cate_commonfan);
//                        searchPresenter.getSearchResult(searchCondition,false);
//                        break;
//                    case R.id.searchmenu_action_taobao :
//                         changeSearchType(SearchCondition.search_cate_taobao);
//                         searchPresenter.getSearchResult(searchCondition,false);
//                         break;
//                    default :
//                        break;
//                }
//                searchleftBtn.setText("综合排序");
//                searchResultRview.scrollToPosition(0);
//                SearchTypeButton.setText(item.getTitle());
//                popupMenu.dismiss();
//                return false;
//            }
//        });
//
//        try {
//            Field field = popupMenu.getClass().getDeclaredField("mPopup");
//            field.setAccessible(true);
//            MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
//            mHelper.setForceShowIcon(true);
//        } catch (Exception e) {
//            Log.e("popupmenu refeclt error",e.getMessage());
//        }
//        popupMenu.show();
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFlagment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFlagment newInstance(String searchCateString) {
        SearchFlagment fragment = new SearchFlagment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1,searchCateString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    private void initIconFont(){
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(),"iconfront/iconfont.ttf");
        toTopIcon.setTypeface(iconfont);
    }

    //-----------------popupwindow 相关操作----------------------------------------

    /**
     * 必须关闭浮层后再更新窗口
     * @param view
     */
    private void updatePopupWindow(View view){
        if(popupWindow == null){
            return;
        }
        if(mactivity == null || mactivity.isFinishing()) {
            return;
        }
        popupWindow.setContentView(view);
    }

    //初始化搜索浮框
    private void initPopupWindow(){
        leftSearchView = layoutInflater.inflate(R.layout.flagment_search_left_area, null);
        leftBelowBackGroup = leftSearchView.findViewById(R.id.search_left_below_backGroup);

        RxView.clicks(leftBelowBackGroup).throttleFirst(1000,TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                   dismissPopupWindow();
            }
        });
        //初始化recycleView;
        searchLeftRview = (RecyclerView) leftSearchView.findViewById(R.id.fragmentSearchLeftRview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchLeftRview.setLayoutManager(linearLayoutManager);
        searchLeftRview.addItemDecoration(new DividerItemDecoration(getActivity(),LinearLayout.VERTICAL));
        searchLeftRviewAdapter = new SearchLeftRviewAdapter();
        searchLeftRviewAdapter.setOnRowClick(new SearchLeftRviewAdapter.OnRowClick() {
            @Override
            public void onClick(View view, SearchLeftRviewAdapter.RowItem rowItem) {
                String currentSearchCate = searchCondition.getCurrentSearchCate();
                if(SearchCondition.search_cate_superfan.equals(currentSearchCate)){
                    searchCondition.updateSearchKeyValue("sortType",rowItem.getSortCode());
                }
                else if(SearchCondition.search_cate_commonfan.equals(currentSearchCate)){
                    searchCondition.updateSearchKeyValue("sortType",rowItem.getSortCode());
                }
                else if(SearchCondition.search_cate_taobao.equals(currentSearchCate)){
                    searchCondition.updateSearchKeyValue("sort",rowItem.getSortCode());
                }
                dismissPopupWindow();
                searchleftBtn.setText(rowItem.getRowDesc());
                searchPresenter.getSearchResult(searchCondition,false);
            }
        });
        searchLeftRview.setAdapter(searchLeftRviewAdapter);

        // 创建一个PopupWindow
        // 参数1：contentView 指定PopupWindow的内容
        // 参数2：width 指定PopupWindow的width
        // 参数3：height 指定PopupWindow的height
        popupWindow = new PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });

        // 获取屏幕和PopupWindow的width和height
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenWidth = getResources().getDisplayMetrics().heightPixels;
        mPopupWindowWidth = popupWindow.getWidth();
        mPopupWindowHeight = popupWindow.getHeight();
    }

    //初始化右边搜索栏的搜索数据
    private View getRightSearchView(int layoutId){
         View contentView = null;
         if(rightSearchViewMap.containsKey(layoutId)){
             contentView = rightSearchViewMap.get(layoutId);
         }
         else if(layoutId ==R.layout.flagment_search_right_area_taobao){
             contentView = layoutInflater.inflate(layoutId,null);
             EditText taobao_startPrice = (EditText) contentView.findViewById(R.id.taobao_search_right_startprice);
             EditText taobao_endPrice = (EditText) contentView.findViewById(R.id.taobao_search_right_endprice);
             CheckBox taobao_mfreeCheck = (CheckBox) contentView.findViewById(R.id.taobao_search_service_mfree_check);
             CheckBox tapbao_tmallCheck = (CheckBox) contentView.findViewById(R.id.taobao_search_service_tmall_check);
             Button taobao_submit_btn = (Button) contentView.findViewById(R.id.taobao_search_condition_submit_btn);
             View taobao_backgroud = contentView.findViewById(R.id.taobao_search_right_below_backGroup);
             //配置编辑框属性
             taobao_startPrice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             taobao_endPrice.setInputType(EditorInfo.TYPE_CLASS_PHONE);

             RxTextView.textChanges(taobao_startPrice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("start_price",charSequence.toString());
                 }
             });
             RxTextView.textChanges(taobao_endPrice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("end_price",charSequence.toString());
                 }
             });
             taobao_mfreeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     SearchCondition.SearchItem filter = searchCondition.getSearchItems().get("filter");
                     String values = (String) filter.getValues();
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("filter",values+"service_myf,");
                     }else{
                         searchCondition.updateSearchKeyValue("filter",values.replace("service_myf,",""));
                     }
                 }
             });
             tapbao_tmallCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     SearchCondition.SearchItem filter = searchCondition.getSearchItems().get("filter");
                     String values = (String) filter.getValues();
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("filter",values+"tab_mall,");
                     }else{
                         searchCondition.updateSearchKeyValue("filter",values.replace("tab_mall,",""));
                     }
                 }
             });
             RxView.clicks(taobao_submit_btn).throttleFirst(1000,TimeUnit.MILLISECONDS).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                      dismissPopupWindow();
                      searchCondition.updateSearchKeyValue("page",1);
                      searchPresenter.getSearchResult(searchCondition,false);
                 }
             });

             RxView.clicks(taobao_backgroud).throttleFirst(1000,TimeUnit.MILLISECONDS).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                     dismissPopupWindow();
                 }
             });
             setupUI(contentView,getActivity());
             //// TODO: 2016/7/7 初始化一些事件;
             rightSearchViewMap.put(layoutId,contentView);
         }else if(layoutId == R.layout.flagment_search_right_area_chaojifan){
             contentView = layoutInflater.inflate(layoutId,null);
             EditText chaojifan_startprice = (EditText) contentView.findViewById(R.id.chaojifan_search_right_startprice);
             EditText chaojifan_endprice = (EditText) contentView.findViewById(R.id.chaojifan_search_right_endprice);
             EditText chaojifan_startfanli = (EditText) contentView.findViewById(R.id.chaojifan_search_right_startfanli);
             EditText chaojifan_endfanli = (EditText) contentView.findViewById(R.id.chaojifan_search_right_endfanli);
             final CheckBox chaojifan_taobao_check = (CheckBox) contentView.findViewById(R.id.chaojifan_search_service_taobao_check);
             final CheckBox chaojifan_tmall_check = (CheckBox) contentView.findViewById(R.id.chaojifan_search_service_tmall_check);
             final CheckBox chaojifan_tmallvip_check = (CheckBox) contentView.findViewById(R.id.chaojifan_search_service_tmallvip_check);
             Button chaojifan_submit_button = (Button) contentView.findViewById(R.id.chaojifan_search_condition_submit_btn);
             View chaojifan_backgroud = contentView.findViewById(R.id.chaojifan_search_right_below_backGroup);
             //设置编辑框属性
             chaojifan_startprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             chaojifan_endprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             chaojifan_startfanli.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             chaojifan_endfanli.setInputType(EditorInfo.TYPE_CLASS_PHONE);

             RxTextView.textChanges(chaojifan_startprice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                      searchCondition.updateSearchKeyValue("startPrice",charSequence.toString());
                 }
             });

             RxTextView.textChanges(chaojifan_endprice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("endPrice",charSequence.toString());
                 }
             });

             RxTextView.textChanges(chaojifan_startfanli).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("startTkRate",charSequence.toString());
                 }
             });

             RxTextView.textChanges(chaojifan_endfanli).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("endTkRate",charSequence.toString());
                 }
             });

             chaojifan_taobao_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("userType","0");
                         chaojifan_tmall_check.setVisibility(View.GONE);
                         chaojifan_tmallvip_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("userType","");
                         chaojifan_tmall_check.setVisibility(View.VISIBLE);
                         chaojifan_tmallvip_check.setVisibility(View.VISIBLE);
                     }
                 }
             });

             chaojifan_tmall_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("userType","1");
                         chaojifan_taobao_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("userType","");
                         chaojifan_taobao_check.setVisibility(View.VISIBLE);

                     }
                 }
             });

             chaojifan_tmallvip_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("b2c","1");
                         searchCondition.updateSearchKeyValue("shopTag","b2c");
                         chaojifan_taobao_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("b2c","");
                         searchCondition.updateSearchKeyValue("shopTag","");
                         chaojifan_taobao_check.setVisibility(View.VISIBLE);
                     }
                 }
             });

             RxView.clicks(chaojifan_submit_button).throttleFirst(1000,TimeUnit.MILLISECONDS)
                     .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                     .subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                     dismissPopupWindow();
                     searchCondition.updateSearchKeyValue("toPage",1);
                     searchPresenter.getSearchResult(searchCondition,false);
                 }
             });

             RxView.clicks(chaojifan_backgroud).throttleFirst(1000,TimeUnit.MILLISECONDS).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                     dismissPopupWindow();
                 }
             });

             setupUI(contentView,getActivity());
             // TODO: 2016/7/12
             rightSearchViewMap.put(layoutId,contentView);
         }else if(layoutId == R.layout.flagment_search_right_area_fanli){
             contentView = layoutInflater.inflate(layoutId,null);

             EditText fanli_startprice = (EditText) contentView.findViewById(R.id.fanli_search_right_startprice);
             EditText fanli_endprice = (EditText) contentView.findViewById(R.id.fanli_search_right_endprice);
             EditText fanli_startfanli = (EditText) contentView.findViewById(R.id.fanli_search_right_startfanli);
             EditText fanli_endfanli = (EditText) contentView.findViewById(R.id.fanli_search_right_endfanli);
             final CheckBox fanli_taobao_check = (CheckBox) contentView.findViewById(R.id.fanli_search_service_taobao_check);
             final CheckBox fanli_tmall_check = (CheckBox) contentView.findViewById(R.id.fanli_search_service_tmall_check);
             final CheckBox fanli_tmallvip_check = (CheckBox) contentView.findViewById(R.id.fanli_search_service_tmallvip_check);
             final CheckBox fanli_mfree_check = (CheckBox) contentView.findViewById(R.id.fanli_search_service_mfree_check);
             Button fanli_submit_button = (Button) contentView.findViewById(R.id.fanli_search_condition_submit_btn);
             View fanli_backgroud = contentView.findViewById(R.id.fanli_search_right_below_backGroup);
             //设置编辑框属性
             fanli_startprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             fanli_endprice.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             fanli_startfanli.setInputType(EditorInfo.TYPE_CLASS_PHONE);
             fanli_endfanli.setInputType(EditorInfo.TYPE_CLASS_PHONE);

             RxTextView.textChanges(fanli_startprice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("startPrice",charSequence.toString());
                 }
             });

             RxTextView.textChanges(fanli_endprice).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("endPrice",charSequence.toString());
                 }
             });

             RxTextView.textChanges(fanli_startfanli).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("startTkRate",charSequence.toString());
                 }
             });

             RxTextView.textChanges(fanli_endfanli).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).subscribe(new Action1<CharSequence>() {
                 @Override
                 public void call(CharSequence charSequence) {
                     searchCondition.updateSearchKeyValue("endTkRate",charSequence.toString());
                 }
             });

             fanli_taobao_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("userType","0");
                         fanli_tmall_check.setVisibility(View.GONE);
                         fanli_tmallvip_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("userType","");
                         fanli_tmall_check.setVisibility(View.VISIBLE);
                         fanli_tmallvip_check.setVisibility(View.VISIBLE);
                     }
                 }
             });

             fanli_tmall_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("userType","1");
                         fanli_taobao_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("userType","");
                         fanli_taobao_check.setVisibility(View.VISIBLE);

                     }
                 }
             });

             fanli_tmallvip_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     SearchCondition.SearchItem userType = searchCondition.getSearchItems().get("userType");
                     String values = (String) userType.getValues();
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("b2c","1");
                         searchCondition.updateSearchKeyValue("shopTag","b2c");
                         fanli_taobao_check.setVisibility(View.GONE);
                     }else{
                         searchCondition.updateSearchKeyValue("b2c","");
                         searchCondition.updateSearchKeyValue("shopTag","");
                         fanli_taobao_check.setVisibility(View.VISIBLE);
                     }
                 }
             });

             fanli_mfree_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                 @Override
                 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         searchCondition.updateSearchKeyValue("freeShipment","1");
                     }else{
                         searchCondition.updateSearchKeyValue("freeShipment","");
                     }
                 }
             });

             RxView.clicks(fanli_submit_button).throttleFirst(1000,TimeUnit.MILLISECONDS)
                     .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                     .subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                     dismissPopupWindow();
                     searchCondition.updateSearchKeyValue("toPage",1);
                     searchPresenter.getSearchResult(searchCondition,false);
                 }
             });

             RxView.clicks(fanli_backgroud).throttleFirst(1000,TimeUnit.MILLISECONDS)
                     .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                     .subscribe(new Action1<Void>() {
                 @Override
                 public void call(Void aVoid) {
                     dismissPopupWindow();
                 }
             });

             setupUI(contentView,getActivity());
             // TODO: 2016/7/12
             rightSearchViewMap.put(layoutId,contentView);
         }
         return contentView;
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

    private void changeSearchType(String typeName){
        searchCondition = SearchCondition.newInstanceWithQ(typeName,q);
        if(typeName.equals(SearchCondition.search_cate_taobao)){
           searchLeftRviewAdapter.setData(SearchLeftRviewAdapter.rowItemsForTaobaoSearch);
           searchLeftRviewAdapter.notifyDataSetChanged();
           rightSearchView = getRightSearchView(R.layout.flagment_search_right_area_taobao);
        }else if(typeName.equals(SearchCondition.search_cate_superfan)){
           searchLeftRviewAdapter.setData(SearchLeftRviewAdapter.rowItemsForSuperfanSearch);
           searchLeftRviewAdapter.notifyDataSetChanged();
           rightSearchView = getRightSearchView(R.layout.flagment_search_right_area_chaojifan);
        }else if(typeName.equals(SearchCondition.search_cate_commonfan)){
           searchLeftRviewAdapter.setData(SearchLeftRviewAdapter.rowItemsForCommonfanSearch);
           searchLeftRviewAdapter.notifyDataSetChanged();
           rightSearchView = getRightSearchView(R.layout.flagment_search_right_area_fanli);
        }

   }

   //刷新 当前flagment
   public void refreshSearchView(SeartchPo po){
       String q = po.getQ();
       String searchType = po.getSearchType();
       //判断是否需要刷新
       if(this.q.equals(q) && searchCondition.getCurrentSearchCate().equals(searchType)){
           return;
       }
       this.q = q;
       rightSearchViewMap.clear();
       changeSearchType(searchType);
       searchCondition.updateSearchKeyValue("q",q);
       SearchTypeButton.setText(SearchCondition.getSearchTypeDesc(searchCondition.getCurrentSearchCate()));
       searchText.setText(q);
       searchPresenter.getSearchResult(searchCondition,false);
   }

   //刷新flagment 数据
   public void refreshSearchData(SeartchPo po){
       String q = po.getQ();
       this.q = q;
   }

    /**
     *  关掉popup window
     */
    private void dismissPopupWindow(){
        if(popupWindow != null && mactivity != null){
            popupWindow.dismiss();
            popupWindow.setFocusable(false);
        }
    }

    /**
     * 释放相关 popupwindow 的资源
     */
    private void destoryPopupWindow() {
        if(searchTypeMenu != null){
            searchTypeMenu.dismissWindow();
            searchTypeMenu = null;
        }
        if(popupWindow != null){
             this.dismissPopupWindow();
             popupWindow = null;
        }
    }

    //---------------------------------实现VIEW 层接口-----------------------------------------------

    /**
     *  更新返利信息
      * @param fanliView
     * @param vo
     * @param isError
     */
    @Override
    public void updateFanliInfo(View fanliView, TaoBaoItemVo vo, boolean isError) {
        TextView discount = (TextView) fanliView.findViewById(R.id.myorder_item_good_discount);
        TextView priceSaved = (TextView) fanliView.findViewById(R.id.myorder_item_good_pricesaved);
        if(isError){
            priceSaved.setVisibility(View.GONE);
            discount.setText("无返利信息");
            return;
        }
        if(null != vo.getJfbCount()){
            if(vo.getJfbCount() == 0){
                discount.setText("无返利信息");
            }else{
                discount.setText("返集分宝 "+vo.getJfbCount());
            }
            priceSaved.setVisibility(View.GONE);
        }else if(null != vo.getTkRate()) {
            discount.setText("返" + DateUtils.getFormatFloat(vo.getTkRate()) + "%");
            priceSaved.setVisibility(View.VISIBLE);
            priceSaved.setText("约" + DateUtils.getFormatFloat(vo.getTkCommFee()) + "元");
        }else{
            discount.setText("无返利信息");
            priceSaved.setVisibility(View.GONE);
        }
    }

    /**
     * 更新商品列表
     * @param goodsList
     */
    @Override
    public void setGoodsList(List<TaoBaoItemVo> goodsList,boolean isReflash) {
        if(isReflash){
            tbGoodDataAdapter.appendDataList(goodsList);
        }else{
          tbGoodDataAdapter.setData(goodsList);
        }
        tbGoodDataAdapter.notifyDataSetChanged();
        if(!isReflash){
          searchResultRview.scrollToPosition(0);
        }
    }

    /**
     *   当搜索结果为空,呈现此对话框
     */
    @Override
    public void setAlertDialogIfNull(){
        final AlertDialog alertDialog = new AlertDialog.Builder(mactivity).create();

        View dialog = layoutInflater.inflate(R.layout.search_null_content_dialog, null);
        String currentSearchCate = searchCondition.getCurrentSearchCate();

        RecyclerView rview = (RecyclerView) dialog.findViewById(R.id.search_null_content_dialog_rview);
        TextView title = (TextView) dialog.findViewById(R.id.search_null_content_dialog_top);
        title.setText(SearchCondition.getSearchTypeDesc(currentSearchCate)+"无搜索结果");
        SearchMenuAdapter adapter = new SearchMenuAdapter();
        adapter.setData(adapter.getNcRowItems(currentSearchCate));
        rview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mactivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rview.setLayoutManager(linearLayoutManager);
        rview.addItemDecoration(new ItemDecoration(mactivity));
        rview.setAdapter(adapter);

        adapter.setOnRowClick(new SearchMenuAdapter.OnRowClick() {
            @Override
            public void onClick(View view, SearchMenuAdapter.RowItem rowItem) {
                String sortCode = rowItem.getSortCode();
                if(!"cancel".equals(sortCode)){
                   SeartchPo po = new SeartchPo();
                   po.setQ(q);
                   po.setSearchType(sortCode);
                   refreshSearchView(po);
                   alertDialog.dismiss();
                }
                else if("cancel".equals(sortCode)){
                    alertDialog.dismiss();
                }
            }
        });

        int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
        alertDialog.show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = CommonUtils.getIntHundred(mScreenWidth*0.6f);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        alertDialog.setContentView(dialog);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public Context getMyContext() {
        return mactivity;
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
