package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.umeng.analytics.MobclickAgent;

import org.xml.sax.XMLReader;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.commonlib.utils.SeartchPo;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.presenter.impl.MainPresenter;
import cn.fuyoushuo.fqbb.presenter.impl.SelectedGoodPresenter;
import cn.fuyoushuo.fqbb.view.Layout.ItemDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.activity.HelpActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.activity.UserLoginActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;
import cn.fuyoushuo.fqbb.view.adapter.GoodDataAdapter;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;
import cn.fuyoushuo.fqbb.view.listener.MyTagHandler;
import cn.fuyoushuo.fqbb.view.view.MainView;
import rx.functions.Action1;

/**
 *  main activity
 */
public class MainFlagment extends BaseFragment implements MainView {

    MainActivity parentActivity;

    @Bind(R.id.main_bottomRcycleView)
    RecyclerView mainBottomRView;

    @Bind(R.id.main_flagment_refreshLayout)
    RefreshLayout refreshLayout;

    @Bind(R.id.main_totop_area)
    View toTopView;

    @Bind(R.id.main_totop_icon)
    TextView toTopIcon;

    @Bind(R.id.main_feedback)
    View mainFeedback;

    private MainPresenter mainPresenter;

    //private CatesDataAdapter fcatesDataAdapter;
    private GoodDataAdapter fgoodDataAdapter;

    LayoutInflater layoutInflater;

    //recycleview 的头部
    View mainFlagmentHeader;

    @Bind(R.id.main_toolbar_searchLayout)
    View mainSearchInputLayout;

    RelativeLayout mainJdArea;

    RelativeLayout mainTbArea;

    RelativeLayout mainTmArea;

    RelativeLayout myTbArea;

    View chaojifanArea;

    View jiukuaijiuArea;

    View chaoliuArea;

    View chihuoArea;

    public MainFlagment() {
        // Required empty public constructor
    }

    @Override
    public int getRootLayoutId() {
        return R.layout.flagment_main;
    }

    @Override
    public void initData() {
        mainPresenter = new MainPresenter(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
//        RxView.clicks(moreArea).throttleFirst(1000, TimeUnit.MILLISECONDS)
//                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .subscribe(new Action1<Void>() {
//            @Override
//            public void call(Void aVoid) {
//                Intent intent = new Intent(getActivity(), SearchActivity.class);
//                if(fcatesDataAdapter.getItemCount() != 0){
//                    int position = fcatesDataAdapter.getCurrentPosition();
//                    String cateName = fcatesDataAdapter.getItem(position).getName();
//                    intent.putExtra("searchKey",cateName);
//                }else{
//                    intent.putExtra("searchKey","");
//                }
//                intent.putExtra("intentFromMainMore", true);
//                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                startActivity(intent);
//            }
//        });
        //
        RxView.clicks(toTopView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mainBottomRView.scrollToPosition(0);
                toTopView.setVisibility(View.GONE);
            }
        });

        RxView.clicks(mainSearchInputLayout).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        MobclickAgent.onEvent(getActivity(),EventIdConstants.HOME_TOP_SEARCH_BTN);
                        RxBus.getInstance().send(new SearchTextClickEvent());
                    }
        });

        RxView.clicks(mainFeedback).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        FeedbackAPI.openFeedbackActivity(MyApplication.getContext());
                    }
        });

        //超级返
        RxView.clicks(chaojifanArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else{
                             SuperfanDialogFragment.newInstance().show(getFragmentManager(),"SuperfanDialogFragment");
                        }
                    }
                });
        //九块九
        RxView.clicks(jiukuaijiuArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else {
                            JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.JKJ_CHANNEL, "九块九还包邮", "").show(getFragmentManager(), "JxspDetailDialogFragment");
                        }
                    }
                });
        //潮流
        RxView.clicks(chaoliuArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else {
                            JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.IFI_CHANNEL,"潮流穿搭","").show(getFragmentManager(),"JxspDetailDialogFragment");
                        }
                    }
                });
        //吃货
        RxView.clicks(chihuoArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else {
                            JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.HCH_CHANNEL, "吃货盛宴", "").show(getFragmentManager(), "JxspDetailDialogFragment");
                        }
                    }
                });


        //京东
        RxView.clicks(mainJdArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(mainPresenter.isNeedTip(2)){
                            setTipDialogIfNeed(2);
                        }else {
                            JdWebviewDialogFragment.newInstance("http://m.jd.com").show(getFragmentManager(),"JdWebviewDialogFragment");
                        }
                    }
                });

        //淘宝
        RxView.clicks(mainTbArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/15
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else {
                            Intent intent = new Intent(getActivity(), WebviewActivity.class);
                            intent.putExtra("loadUrl","https://m.taobao.com");
                            startActivity(intent);
                        }
                    }
                });

        //天猫
        RxView.clicks(mainTmArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/14
                        if(mainPresenter.isNeedTip(1)){
                            setTipDialogIfNeed(1);
                        }else {
                            Intent intent = new Intent(getActivity(), WebviewActivity.class);
                            intent.putExtra("loadUrl","https://www.tmall.com/?from=m");
                            startActivity(intent);
                        }
                    }
                });

        //我的淘宝
        RxView.clicks(myTbArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/14
                        Intent intent = new Intent(getActivity(), WebviewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra("loadUrl", "https://h5.m.taobao.com/mlapp/mytaobao.html#mlapp-mytaobao");
                        intent.putExtra("forSearchGoodInfo", false);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void initView() {
        parentActivity = (MainActivity) this.getActivity();

        mainPresenter.getFGoods(0l, 1, false);

        mainFlagmentHeader = layoutInflater.inflate(R.layout.flagment_main_header, null);

        chaojifanArea = mainFlagmentHeader.findViewById(R.id.card_chaojifan);

        jiukuaijiuArea = mainFlagmentHeader.findViewById(R.id.card_jkj);

        chaoliuArea  = mainFlagmentHeader.findViewById(R.id.card_chaoliu);

        chihuoArea = mainFlagmentHeader.findViewById(R.id.card_chihuo);

        mainJdArea = (RelativeLayout) mainFlagmentHeader.findViewById(R.id.main_jd_icon);

        mainTbArea = (RelativeLayout) mainFlagmentHeader.findViewById(R.id.main_tb_icon);

        mainTmArea = (RelativeLayout) mainFlagmentHeader.findViewById(R.id.main_tm_icon);

        myTbArea = (RelativeLayout) mainFlagmentHeader.findViewById(R.id.main_mytb_icon);

        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                Long cateId = fgoodDataAdapter.getCateId();
                Integer page = fgoodDataAdapter.getCurrentPage();
                mainPresenter.getFGoods(cateId, page + 1, false);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Long cateId = fgoodDataAdapter.getCateId();
                Integer page = fgoodDataAdapter.getCurrentPage();
                mainPresenter.getFGoods(cateId, 1, true);
                refreshLayout.setRefreshing(false);
                return;
            }
        });

        mainBottomRView.setHasFixedSize(true);
        //mainBottomRView.addItemDecoration(new GoodItemsDecoration(10,5));
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(mactivity, 2);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        mainBottomRView.setLayoutManager(gridLayoutManager);
        fgoodDataAdapter = new GoodDataAdapter(mainFlagmentHeader);
        fgoodDataAdapter.setOnLoadImage(new GoodDataAdapter.OnLoad() {
            @Override
            public void onLoadImage(SimpleDraweeView view, FGoodItem goodItem) {
                int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
                int intHundred = CommonUtils.getIntHundred(mScreenWidth / 2);
                if (intHundred > 800) {
                    intHundred = 800;
                }
                if (!BaseActivity.isTablet(mactivity)) {
                    intHundred = 400;
                }
                String url = goodItem.getImageUrl();
                url = url.replace("180x180", intHundred + "x" + intHundred);
                view.setAspectRatio(1.0F);
                view.setImageURI(Uri.parse(url));
            }

            @Override
            public void onGoodItemClick(View clickView, FGoodItem goodItem) {
                if(mainPresenter.isNeedTip(1)){
                    setTipDialogIfNeed(1);
                }else {
                  String url = goodItem.getItemUrl();
                  MainActivity ma = (MainActivity) getActivity();
                  //ma.showWebviewFragment(url, false,false);
                  Intent intent = new Intent(getActivity(), WebviewActivity.class);
                  intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                  intent.putExtra("loadUrl", url);
                  intent.putExtra("forSearchGoodInfo", false);
                  startActivity(intent);
                }
            }
        });
        mainBottomRView.setAdapter(fgoodDataAdapter);
        mainBottomRView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (gridLayoutManager.findFirstVisibleItemPosition() == 0) {
                    toTopView.setVisibility(View.GONE);
                }
                if (gridLayoutManager.findFirstVisibleItemPosition() != 0) {
                    toTopView.setVisibility(View.VISIBLE);
                }
            }
        });

        initIconFront();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return a new instance of fragment MainFlagment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFlagment newInstance() {
        MainFlagment fragment = new MainFlagment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mainPresenter != null) {
            mainPresenter.onDestroy();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        layoutInflater = LayoutInflater.from(getActivity());
    }

    //初始化字体图标
    private void initIconFront() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfront/iconfont.ttf");
        toTopIcon.setTypeface(iconfont);
    }

    //----------------------------view 接口实现-----------------------

    @Override
    public void setupFcatesView(List<FCateItem> cateItems) {
        if(cateItems != null && !cateItems.isEmpty()){
           cateItems.get(0).setIsRed(true);
        }
        //fcatesDataAdapter.setData(cateItems);
        //fcatesDataAdapter.notifyDataSetChanged();
    }


    @Override
    public void setupFgoodsView(Integer page, Long cateId, List<FGoodItem> goodItems, boolean isRefresh) {
        if (isRefresh) {
            fgoodDataAdapter.setData(goodItems);
        } else {
            fgoodDataAdapter.appendDataList(goodItems);
        }
        fgoodDataAdapter.setCateId(cateId);
        fgoodDataAdapter.setCurrentPage(page);
        fgoodDataAdapter.notifyDataSetChanged();
    }

    /**
     *  1.淘宝 2.京东
     * @param flag
     */
    public void setTipDialogIfNeed(final int flag){
        final AlertDialog alertDialog = new AlertDialog.Builder(mactivity).create();
        View dialog = layoutInflater.inflate(R.layout.main_tip_content_dialog, null);
        TextView content = (TextView) dialog.findViewById(R.id.main_tip_content);
        Button leftButton = (Button) dialog.findViewById(R.id.leftCommit);
        Button rightButton = (Button) dialog.findViewById(R.id.rightCommit);
        RxView.clicks(leftButton).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         mainPresenter.setTipState(flag,true);
                         alertDialog.dismiss();
                    }
                });

        RxView.clicks(rightButton).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          mainPresenter.setTipState(flag,false);
                          alertDialog.dismiss();
                    }
                });

        String htmlForTaobao = "1.商品详情页才会显示返利信息<br>2.进入返钱模式才能拿到返利<br>3.新用户需要进行身份验证后才能进入返钱模式<br>4.通常付款后5分钟就可以查到返利订单<br>5.确认收货后次月20日结算返利";
        String htmlForJd = "1.导致京东订单失效原因非常多，务必了解后再购买<br><font color=\"blue\"><jdReason>京东订单失效原因</jdReason></font><br>2.进入返钱模式才能拿到返利<br>3.新用户需要登陆返钱宝宝账号后才能进入返钱模式<br>4.提交订单后无需付款，每个整点过10分钟时同步订单，建议检查后再付款<br><font color=\"red\">京东订单不保证返利效果，介意者慎用</font>";
        if(flag == 1){
            content.setText(Html.fromHtml(htmlForTaobao));
        }
        else if(flag == 2){
            content.setText(Html.fromHtml(htmlForJd,null,new MyTagHandler("jdReason", new MyTagHandler.OnClickTag() {
                @Override
                public void onClick() {
                    Intent intent = new Intent(mactivity, HelpActivity.class);
                    intent.putExtra("jdOrderNoEffect",true);
                    startActivity(intent);
                }

            })));
            content.setHighlightColor(getResources().getColor(android.R.color.transparent));
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
        else{

        }
        int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
        alertDialog.show();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.width = CommonUtils.getIntHundred(mScreenWidth*0.8f);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes(params);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(0, 0, 0, 0)));
        alertDialog.setContentView(dialog);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    //---------------------------- 自定义事件-----------------------------------

    /**
     * 搜索框点击触发事件
     */
    public class SearchTextClickEvent extends RxBus.BusEvent {

    }


    //------------------------------页面统计----------------------------------------


}