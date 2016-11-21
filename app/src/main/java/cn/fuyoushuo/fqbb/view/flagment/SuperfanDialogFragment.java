package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.domain.entity.FCateItem;
import cn.fuyoushuo.fqbb.domain.entity.FGoodItem;
import cn.fuyoushuo.fqbb.presenter.impl.MainPresenter;
import cn.fuyoushuo.fqbb.view.Layout.CateItemsDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;
import cn.fuyoushuo.fqbb.view.adapter.CatesDataAdapter;
import cn.fuyoushuo.fqbb.view.adapter.FgoodDataAdapter;
import cn.fuyoushuo.fqbb.view.view.MainView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/10/27.
 */
public class SuperfanDialogFragment extends RxDialogFragment implements MainView{


    @Bind(R.id.superfan_backArea)
    View backArea;

    @Bind(R.id.superfan_topRcycleView)
    RecyclerView topRview;

    @Bind(R.id.superfan_bottomRcycleView)
    RecyclerView bottomRview;

    @Bind(R.id.superfan_flagment_refreshLayout)
    RefreshLayout refreshLayout;

    @Bind(R.id.main_totop_icon)
    TextView toTopIcon;

    @Bind(R.id.main_totop_area)
    View toTopView;

    CatesDataAdapter fcatesDataAdapter;

    FgoodDataAdapter fgoodDataAdapter;

    MainPresenter mainPresenter;

    public static SuperfanDialogFragment newInstance(){
        SuperfanDialogFragment adf = new SuperfanDialogFragment();
        return adf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPresenter = new MainPresenter(this);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.view_superfan_page,container);
        ButterKnife.bind(this,inflate);
        fcatesDataAdapter = new CatesDataAdapter();
        fgoodDataAdapter = new FgoodDataAdapter();
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

        topRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        topRview.setLayoutManager(linearLayoutManager1);
        topRview.addItemDecoration(new CateItemsDecoration());
        fcatesDataAdapter.setOnCateClick(new CatesDataAdapter.OnCateClick(){
            @Override
            public void onClick(View view, FCateItem cateItem, int lastPosition) {
                cateItem.setIsRed(true);
                FCateItem item = fcatesDataAdapter.getItem(lastPosition);
                item.setIsRed(false);
                fcatesDataAdapter.notifyDataSetChanged();
                mainPresenter.getFGoods(cateItem.getId(), 1, true);
                //布局移动到顶部
                bottomRview.scrollToPosition(0);
            }
        });

        topRview.setAdapter(fcatesDataAdapter);

        bottomRview.setHasFixedSize(true);
        //mainBottomRView.addItemDecoration(new GoodItemsDecoration(10,5));
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        bottomRview.setLayoutManager(gridLayoutManager);
        fgoodDataAdapter.setOnLoad(new FgoodDataAdapter.OnLoad() {
            @Override
            public void onLoadImage(SimpleDraweeView view, FGoodItem goodItem) {
                int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
                int intHundred = CommonUtils.getIntHundred(mScreenWidth / 2);
                if (intHundred > 800) {
                    intHundred = 800;
                }
                if (!BaseActivity.isTablet(getActivity())) {
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
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        bottomRview.addOnScrollListener(
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
        bottomRview.setAdapter(fgoodDataAdapter);
//        bottomRview.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (gridLayoutManager.findFirstVisibleItemPosition() == 0) {
//                    //toTopView.setVisibility(View.GONE);
//                }
//                if (gridLayoutManager.findFirstVisibleItemPosition() != 0) {
//                    //toTopView.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        initIconFront();
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainPresenter.getFcates();
        mainPresenter.getFGoods(100l, 1, false);

        RxView.clicks(backArea).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         dismissAllowingStateLoss();
                    }
                });

        RxView.clicks(toTopView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        bottomRview.scrollToPosition(0);
                        toTopView.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainPresenter.onDestroy();
    }

    //初始化字体图标
    private void initIconFront() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfront/iconfont.ttf");
        toTopIcon.setTypeface(iconfont);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("superFanPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("superFanPage");
    }

    //---------------------------------实现VIEW层的接口----------------------------------------------------

    @Override
    public void setupFcatesView(List<FCateItem> cateItems) {
        if(cateItems != null && !cateItems.isEmpty()){
            cateItems.get(0).setIsRed(true);
        }
        fcatesDataAdapter.setData(cateItems);
        fcatesDataAdapter.notifyDataSetChanged();
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
}
