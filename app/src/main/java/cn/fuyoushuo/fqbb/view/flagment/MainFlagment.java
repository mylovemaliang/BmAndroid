package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.presenter.impl.MainPresenter;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.activity.UserLoginActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;
import cn.fuyoushuo.fqbb.view.adapter.GoodDataAdapter;
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

    //ImageCycleView mImageCycleView;

    //private int LUNBO_DELAY_TIME = 2000;

    //public List<ImageCycleView.ImageInfo> listImgsInfo = new ArrayList<ImageCycleView.ImageInfo>();

    private MainPresenter mainPresenter;

    //private CatesDataAdapter fcatesDataAdapter;
    private GoodDataAdapter fgoodDataAdapter;

    RecyclerView mainTopRView;

    LayoutInflater layoutInflater;

    //LinearLayout topMyOrder;

    //LinearLayout topMyJfb;

    //LinearLayout topTixian;

    //View moreArea;

    //recycleview 的头部
    View mainFlagmentHeader;

    @Bind(R.id.main_toolbar_searchLayout)
    View mainSearchInputLayout;

    View chaojifanArea;

    //private SharedPreferences lunboImgInfosCache;

    //private final String lunboImgInfosFile = "fplunbo";

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

        RxView.clicks(chaojifanArea).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //SuperfanDialogFragment.newInstance().show(getFragmentManager(),"superfan_fragment");
                        Intent intent = new Intent(mactivity,UserLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void initView() {
        parentActivity = (MainActivity) this.getActivity();

        //mainPresenter.getFcates();
        mainPresenter.getFGoods(100l, 1, false);

        mainFlagmentHeader = layoutInflater.inflate(R.layout.flagment_main_header, null);

        //mainTopRView = (RecyclerView) mainFlagmentHeader.findViewById(R.id.main_topRcycleView);

        //moreArea = mainFlagmentHeader.findViewById(R.id.main_more_area);

        chaojifanArea = mainFlagmentHeader.findViewById(R.id.card_chaojifan);

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
                //progressBar.setVisibility(View.VISIBLE);
                //int cateCount = fcatesDataAdapter.getItemCount();
//                if(cateCount == 0){
//                    mainPresenter.getFcates();
//                }
                Long cateId = fgoodDataAdapter.getCateId();
                Integer page = fgoodDataAdapter.getCurrentPage();
                mainPresenter.getFGoods(cateId, 1, true);
                refreshLayout.setRefreshing(false);
                return;
            }
        });

//      mainSearchText.setText("请你输入");
//      mainSearchText.clearFocus();

//        mainTopRView.setHasFixedSize(true);
//        final LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(mactivity);
//        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mainTopRView.setLayoutManager(linearLayoutManager1);
//        mainTopRView.addItemDecoration(new CateItemsDecoration());
//        fcatesDataAdapter = new CatesDataAdapter();
//        fcatesDataAdapter.setOnCateClick(new CatesDataAdapter.OnCateClick() {
//            @Override
//            public void onClick(View view, FCateItem cateItem, int lastPosition) {
//
//                cateItem.setIsRed(true);
//                FCateItem item = fcatesDataAdapter.getItem(lastPosition);
//                item.setIsRed(false);
//                fcatesDataAdapter.notifyDataSetChanged();
//
//                mainPresenter.getFGoods(cateItem.getId(), 1, true);
//                //布局移动到顶部
//                mainBottomRView.scrollToPosition(0);
//            }
//        });
//
//      mainTopRView.setAdapter(fcatesDataAdapter);

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
                String url = goodItem.getItemUrl();
                MainActivity ma = (MainActivity) getActivity();
                //ma.showWebviewFragment(url, false,false);

                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        mainBottomRView.setAdapter(fgoodDataAdapter);
        mainBottomRView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

//        mImageCycleView = (ImageCycleView) mainFlagmentHeader.findViewById(R.id.icv_topView);
//        mImageCycleView.setOnPageClickListener(new ImageCycleView.OnPageClickListener() {
//            @Override
//            public void onClick(View imageView, ImageCycleView.ImageInfo imageInfo) {
//                if (imageInfo.value != null && !"".equals(imageInfo.value.toString().trim())) {
//                    Intent intent = new Intent(getActivity(), WebviewActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                    intent.putExtra("loadUrl", imageInfo.value.toString().trim());
//                    intent.putExtra("forSearchGoodInfo", false);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        if (lunboImgInfosCache == null) {
//            lunboImgInfosCache = parentActivity.getApplicationContext().getSharedPreferences(lunboImgInfosFile, Context.MODE_PRIVATE);
//        }
//      initLunbo();//显示轮播图片
//      reloadLunboImg();//重新异步加载新的轮播图片，避免阻塞
        initIconFront();
    }

//    public void reloadLunboImg() {
//        final String lunboImgInfoUrl = "http://www.fanqianbb.com/adv/mpicFp.htm";
//
//        RequestQueue volleyRq = Volley.newRequestQueue(parentActivity.getApplicationContext(), new OkHttpStack(parentActivity.getApplicationContext()));
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, lunboImgInfoUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        if (response != null && !"".equals(response.trim())) {
//                            SharedPreferences.Editor prefsWriter = lunboImgInfosCache.edit();
//                            prefsWriter.putString("lunboImgs", response.trim());
//                            prefsWriter.commit();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                });
//
//        // 把这个请求加入请求队列
//        volleyRq.add(stringRequest);
//    }

//    private void initLunbo() {
//        boolean useLocalImg = false;
//
//        String lunboImgsInfos = lunboImgInfosCache.getString("lunboImgs", "");
//        if (lunboImgsInfos != null && !"".equals(lunboImgsInfos.trim())) {
//            JSONArray imagesListJson = null;
//            try{
//                JSONObject jsonObj = JSONObject.parseObject(lunboImgsInfos.trim());
//                imagesListJson = jsonObj.getJSONArray("r");
//            }catch(Exception e){
//
//            }
//            if (imagesListJson != null && imagesListJson.size() > 0) {
//                listImgsInfo.clear();
//                for (int i = 0; i < imagesListJson.size(); i++) {
//                    JSONObject oneImageInfo = imagesListJson.getJSONObject(i);
//                    String imgUrl = oneImageInfo.getString("imgUrl");
//                    String textContent = oneImageInfo.getString("textContent");
//                    String linkUrl = oneImageInfo.getString("linkUrl");
//                    listImgsInfo.add(new ImageCycleView.ImageInfo(imgUrl, textContent, linkUrl));
//                }
//            } else {
//                listImgsInfo.add(new ImageCycleView.ImageInfo(R.mipmap.lb, "", ""));//默认使用步骤的轮播图，是本地图片
//                useLocalImg = true;
//            }
//        } else {
//            listImgsInfo.add(new ImageCycleView.ImageInfo(R.mipmap.lb, "", ""));//默认使用步骤的轮播图，是本地图片
//            useLocalImg = true;
//        }
//
//        if (useLocalImg) {
//            mImageCycleView.loadData(listImgsInfo, new ImageCycleView.LoadImageCallBack() {
//                @Override
//                public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {
//                    SimpleDraweeView view = new SimpleDraweeView(parentActivity);
//                    view.setImageResource(Integer.parseInt(imageInfo.image.toString()));
//                    return view;
//                }
//            });
//        } else {
//            mImageCycleView.loadData(listImgsInfo, new ImageCycleView.LoadImageCallBack() {
//                @Override
//                public ImageView loadAndDisplay(ImageCycleView.ImageInfo imageInfo) {
//                    SimpleDraweeView view = new SimpleDraweeView(parentActivity);
//                    view.setImageURI(Uri.parse(imageInfo.image.toString()));
//                    return view;
//                }
//            });
//        }
//    }

    //横移标准线
//    private void moveCateItem(final TextView textView, final View redline) {
//        int[] textLocation = new int[2];
//        int[] redlineLocation = new int[2];
//        textView.getLocationOnScreen(textLocation);
//        redline.getLocationOnScreen(redlineLocation);
//        final int cx = redlineLocation[0];
//        final int tox = textLocation[0];
//        int left = redline.getLeft();
//        redline.scrollBy(cx - tox, 0);
//    }

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


    //---------------------------- 自定义事件-----------------------------------

    /**
     * 搜索框点击触发事件
     */
    public class SearchTextClickEvent extends RxBus.BusEvent {

    }


    //------------------------------页面统计----------------------------------------


}