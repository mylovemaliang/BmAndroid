package cn.fuyoushuo.fqbb.view.flagment;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import cn.fuyoushuo.fqbb.domain.entity.TbCateVo;
import cn.fuyoushuo.fqbb.presenter.impl.SelectedGoodPresenter;
import cn.fuyoushuo.fqbb.view.Layout.CateItemsDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.adapter.SelectedCatesDataAdapter;
import cn.fuyoushuo.fqbb.view.adapter.TbGoodDataAdapter;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/10.
 */
public class JxspDetailDialogFragment extends RxDialogFragment {


    @Bind(R.id.jxsp_detail_backArea)
    RelativeLayout backView;

    @Bind(R.id.jxsp_title)
    TextView titleView;

    @Bind(R.id.jxsp_detail_topRcycleView)
    RecyclerView cateRview;

    @Bind(R.id.jxsp_detail_refreshLayout)
    RefreshLayout refreshLayout;

    @Bind(R.id.jxsp_detail_bottomRcycleView)
    RecyclerView goodRview;

    @Bind(R.id.main_totop_icon)
    TextView toTopIcon;

    @Bind(R.id.main_totop_area)
    View toTopView;

    SelectedCatesDataAdapter selectCatesDataAdapter;

    TbGoodDataAdapter tbGoodDataAdapter;

    private String title = "";

    private String currentCatId = "";

    private String currentChannel = "";

    private String originCatId = "";

    private int currentLevel = 0;

    SelectedGoodPresenter selectedGoodPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        if(getArguments() != null){
            this.currentChannel = getArguments().getString("channel","");
            this.title = getArguments().getString("title","");
            this.originCatId = getArguments().getString("catId","");
            this.currentCatId = getArguments().getString("catId","");
        }
        selectedGoodPresenter = new SelectedGoodPresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.view_jxsp_detail_page, container, false);
        ButterKnife.bind(this,inflate);

        selectCatesDataAdapter = new SelectedCatesDataAdapter();
        tbGoodDataAdapter= new TbGoodDataAdapter();
        refreshLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                // TODO: 2016/11/10 获取下一页数据
                final int currentPage = tbGoodDataAdapter.getCurrentPage();
                selectedGoodPresenter.getSelectedGood(currentChannel,currentPage+1,currentCatId, null, new SelectedGoodPresenter.SelectGoodGetCallBack() {
                    @Override
                    public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                        if(tbGoodDataAdapter != null){
                            tbGoodDataAdapter.appendDataList(goodList);
                            tbGoodDataAdapter.notifyDataSetChanged();
                        }
                        tbGoodDataAdapter.setCurrentPage(currentPage+1);
                    }

                    @Override
                    public void onGetGoodFail(String msg) {
                        Toast.makeText(MyApplication.getContext(),"网速稍慢哦,请重试",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: 2016/11/10 刷新当前页面数据
                final int currentPage = tbGoodDataAdapter.getCurrentPage();
                selectedGoodPresenter.getSelectedGood(currentChannel,currentPage,currentCatId,currentLevel, new SelectedGoodPresenter.SelectGoodGetCallBack() {
                    @Override
                    public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                        if(tbGoodDataAdapter != null){
                            tbGoodDataAdapter.setData(goodList);
                            tbGoodDataAdapter.notifyDataSetChanged();
                        }
                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onGetGoodFail(String msg) {
                        Toast.makeText(MyApplication.getContext(),"网速稍慢哦,请重试",Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }
        });

        cateRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        cateRview.setLayoutManager(linearLayoutManager1);
        cateRview.addItemDecoration(new CateItemsDecoration());
        selectCatesDataAdapter.setOnCateClick(new SelectedCatesDataAdapter.OnCateClick() {
            @Override
            public void onClick(View view, TbCateVo cateItem, int lastPosition) {
                cateItem.setRed(true);
                TbCateVo item = selectCatesDataAdapter.getItem(lastPosition);
                item.setRed(false);
                selectCatesDataAdapter.notifyDataSetChanged();
                // TODO: 2016/11/10  获取点击后数据
                currentCatId = getCurrentCatId(cateItem.getCatId());
                int level = cateItem.getLevel();
                currentLevel = level;
                selectedGoodPresenter.getSelectedGood(currentChannel,1,currentCatId,level,new SelectedGoodPresenter.SelectGoodGetCallBack() {
                    @Override
                    public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                        if(tbGoodDataAdapter != null){
                           tbGoodDataAdapter.setData(goodList);
                           tbGoodDataAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onGetGoodFail(String msg) {
                        Toast.makeText(MyApplication.getContext(),"网速稍慢哦,请重试",Toast.LENGTH_SHORT).show();
                    }
                });
                //布局移动到顶部
                goodRview.scrollToPosition(0);
            }
        });

        cateRview.setAdapter(selectCatesDataAdapter);

        goodRview.setHasFixedSize(true);
        //mainBottomRView.addItemDecoration(new GoodItemsDecoration(10,5));
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);

        goodRview.setLayoutManager(gridLayoutManager);
        tbGoodDataAdapter.setOnLoad(new TbGoodDataAdapter.OnLoad() {
            @Override
            public void onLoadImage(SimpleDraweeView view, TaoBaoItemVo goodItem) {
                int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
                int intHundred = CommonUtils.getIntHundred(mScreenWidth / 2);
                if (intHundred > 800) {
                    intHundred = 800;
                }
                if (!BaseActivity.isTablet(getActivity())) {
                    intHundred = 400;
                }
                String url = goodItem.getPic_path();
                url = url.replace("180x180", intHundred + "x" + intHundred);
                view.setAspectRatio(1.0F);
                view.setImageURI(Uri.parse(url));
            }

            @Override
            public void onItemClick(View view, TaoBaoItemVo goodItem) {
                // TODO: 2016/11/10 点击商品逻辑实现
                String goodUrl = goodItem.getUrl();
                if(!TextUtils.isEmpty(goodUrl)) {
                    RxBus.getInstance().send(new JxscToGoodInfoEvent(goodUrl));
                }
            }

            @Override
            public void onFanliInfoLoaded(View fanliView, TaoBaoItemVo taoBaoItemVo) {
                  return;
            }
        });
        goodRview.addOnScrollListener(
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

        goodRview.setAdapter(tbGoodDataAdapter);
        initIconFront();
        return inflate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(toTopView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        goodRview.scrollToPosition(0);
                        toTopView.setVisibility(View.GONE);
                    }
                });

        //返回键处理
        RxView.clicks(backView).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          dismissAllowingStateLoss();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        selectedGoodPresenter.getSelectedGood(currentChannel,1,originCatId,null,new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                TbCateVo all = new TbCateVo("", 0, "全部");
                all.setRed(true);
                if(selectCatesDataAdapter != null){
                    cateList.addFirst(all);
                    selectCatesDataAdapter.setData(cateList);
                    selectCatesDataAdapter.notifyDataSetChanged();
                }
                if(tbGoodDataAdapter != null){
                    tbGoodDataAdapter.setData(goodList);
                    tbGoodDataAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {
                Toast.makeText(MyApplication.getContext(),"网速稍慢哦,请重试",Toast.LENGTH_SHORT).show();
            }
        });

        titleView.setText(title);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectedGoodPresenter.onDestroy();
    }

    //初始化字体图标
    private void initIconFront() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfront/iconfont.ttf");
        toTopIcon.setTypeface(iconfont);
    }


    public static JxspDetailDialogFragment newInstance(String channel,String title,String catId){
        Bundle args = new Bundle();
        args.putString("channel",channel);
        args.putString("title",title);
        args.putString("catId",catId);
        JxspDetailDialogFragment fragment = new JxspDetailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("alimama_channel_"+currentChannel);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageStart("alimama_channel_-"+currentChannel);
    }

    private String getCurrentCatId(String currentCatId){
        if(TextUtils.isEmpty(currentCatId)){
             return originCatId;
        }else{
           if(TextUtils.isEmpty(originCatId)){
               return currentCatId;
           }else{
               return originCatId+","+currentCatId;
           }
        }
    }

   //----------------------------------event时间统计----------------------------------------------

   public class JxscToGoodInfoEvent extends RxBus.BusEvent{

       private String goodUrl;

       public String getGoodUrl() {
           return goodUrl;
       }

       public void setGoodUrl(String goodUrl) {
           this.goodUrl = goodUrl;
       }

       public JxscToGoodInfoEvent(String goodUrl) {
           this.goodUrl = goodUrl;
       }
   }
}
