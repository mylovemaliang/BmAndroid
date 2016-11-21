package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import cn.fuyoushuo.fqbb.domain.entity.TbCateVo;
import cn.fuyoushuo.fqbb.presenter.impl.SelectedGoodPresenter;
import cn.fuyoushuo.fqbb.view.Layout.CateItemsDecoration;
import cn.fuyoushuo.fqbb.view.Layout.SelectedGoodsDecoration;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;
import cn.fuyoushuo.fqbb.view.adapter.SelectedGoodDataAdapter;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/10.
 */
public class SelectedGoodFragment extends BaseFragment{


    @Bind(R.id.tehui_rview)
    RecyclerView tehuiRview;

    SelectedGoodDataAdapter tehuiAdapter;

    @Bind(R.id.nzjh_rview)
    RecyclerView nzRview;

    SelectedGoodDataAdapter nzAdapter;

    @Bind(R.id.lz_rview)
    RecyclerView lzRview;

    SelectedGoodDataAdapter lzAdapter;

    @Bind(R.id.meishi_rview)
    RecyclerView meishiRview;

    SelectedGoodDataAdapter meishiAdapter;

    @Bind(R.id.jiaju_rview)
    RecyclerView jiajuRview;

    SelectedGoodDataAdapter jiajuAdapter;

    @Bind(R.id.sport_rview)
    RecyclerView sportRview;

    SelectedGoodDataAdapter sportAdapter;

    @Bind(R.id.tehui_more)
    TextView tehuiMore;

    @Bind(R.id.nzjh_more)
    TextView nzjhMore;

    @Bind(R.id.lz_more)
    TextView lzMore;

    @Bind(R.id.meishi_more)
    TextView meishiMore;

    @Bind(R.id.jiaju_more)
    TextView jiajuMore;

    @Bind(R.id.sport_more)
    TextView sportMore;

    @Bind(R.id.channel_tehui)
    Button tehuiButton;

    @Bind(R.id.channel_nzjh)
    Button nzjhButton;

    @Bind(R.id.channel_lz)
    Button lzButton;

    @Bind(R.id.channel_meishi)
    Button meishiButton;

    @Bind(R.id.channel_jiaju)
    Button jiajuButton;

    @Bind(R.id.channel_sport)
    Button sportButton;

    @Bind(R.id.selected_swipe_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    SelectedGoodPresenter selectGoodPresent;

    private boolean isLoaded = false;

    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    protected String getPageName() {
        return "jxscPage";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_selected_good;
    }

    @Override
    protected void initData() {
       selectGoodPresent = new SelectedGoodPresenter();
    }


    @Override
    protected void initView() {
        //初始化特价好货
        tehuiAdapter = new SelectedGoodDataAdapter();
        tehuiRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getActivity());
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        tehuiRview.setLayoutManager(linearLayoutManager1);
        tehuiRview.addItemDecoration(new SelectedGoodsDecoration());
        tehuiAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        tehuiRview.setAdapter(tehuiAdapter);

        //初始化女装尖货
        nzAdapter = new SelectedGoodDataAdapter();
        nzRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getActivity());
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        nzRview.setLayoutManager(linearLayoutManager2);
        nzRview.addItemDecoration(new SelectedGoodsDecoration());
        nzAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        nzRview.setAdapter(nzAdapter);

        //男装
        lzAdapter = new SelectedGoodDataAdapter();
        lzRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getActivity());
        linearLayoutManager3.setOrientation(LinearLayoutManager.HORIZONTAL);
        lzRview.setLayoutManager(linearLayoutManager3);
        lzRview.addItemDecoration(new SelectedGoodsDecoration());
        lzAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        lzRview.setAdapter(lzAdapter);

        //美食
        meishiAdapter = new SelectedGoodDataAdapter();
        meishiRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(getActivity());
        linearLayoutManager4.setOrientation(LinearLayoutManager.HORIZONTAL);
        meishiRview.setLayoutManager(linearLayoutManager4);
        meishiRview.addItemDecoration(new SelectedGoodsDecoration());
        meishiAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        meishiRview.setAdapter(meishiAdapter);

        //家居
        jiajuAdapter = new SelectedGoodDataAdapter();
        jiajuRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getActivity());
        linearLayoutManager5.setOrientation(LinearLayoutManager.HORIZONTAL);
        jiajuRview.setLayoutManager(linearLayoutManager5);
        jiajuRview.addItemDecoration(new SelectedGoodsDecoration());
        jiajuAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        jiajuRview.setAdapter(jiajuAdapter);

        //运动
        sportAdapter = new SelectedGoodDataAdapter();
        sportRview.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager6 = new LinearLayoutManager(getActivity());
        linearLayoutManager6.setOrientation(LinearLayoutManager.HORIZONTAL);
        sportRview.setLayoutManager(linearLayoutManager6);
        sportRview.addItemDecoration(new SelectedGoodsDecoration());
        sportAdapter.setOnLoad(new SelectedGoodDataAdapter.OnLoad() {
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
                String url = goodItem.getUrl();
                Intent intent = new Intent(getActivity(), WebviewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("bizString","tbGoodDetail");
                intent.putExtra("loadUrl", url);
                intent.putExtra("forSearchGoodInfo", false);
                startActivity(intent);
            }
        });
        sportRview.setAdapter(sportAdapter);
        super.initView();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshResult();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        //更多按钮的响应
        RxView.clicks(tehuiMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.TEHUI_CHANNEL,"特价好货","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(nzjhMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.NZJH_CHANNEL,"女装尖货","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(lzMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.IFI_CHANNEL,"流行男装","50344007").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(meishiMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.HCH_CHANNEL,"淘宝汇吃","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(jiajuMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.JYJ_CHANNEL,"极有家","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(sportMore).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.KDC_CHANNEL,"酷动城","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        //--------------------------------------对按钮的响应---------------------------------------------------------

        RxView.clicks(tehuiButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.TEHUI_CHANNEL,"特价好货","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(nzjhButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.NZJH_CHANNEL,"女装尖货","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(lzButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.IFI_CHANNEL,"流行男装","50344007").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(meishiButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.HCH_CHANNEL,"淘宝汇吃","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(jiajuButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.JYJ_CHANNEL,"极有家","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });

        RxView.clicks(sportButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        JxspDetailDialogFragment.newInstance(SelectedGoodPresenter.KDC_CHANNEL,"酷动城","").show(getFragmentManager(),"JxspDetailDialogFragment");
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshResult();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        selectGoodPresent.onDestroy();
    }

    //刷新整体的结果页
    public void refreshResult(){
        //刷新特价
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.TEHUI_CHANNEL, 1, null,null,new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                 if(tehuiAdapter != null) {
                     tehuiAdapter.setData(goodList);
                     tehuiAdapter.notifyDataSetChanged();
                 }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });
        //刷新女装
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.NZJH_CHANNEL, 1, null, null, new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                if(nzAdapter != null){
                    nzAdapter.setData(goodList);
                    nzAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });

        //刷新男装
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.IFI_CHANNEL, 1,"50344007",1,new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                if(lzAdapter != null){
                    lzAdapter.setData(goodList);
                    lzAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });

        //刷新美食
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.HCH_CHANNEL, 1, null, null, new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                if(meishiAdapter != null){
                    meishiAdapter.setData(goodList);
                    meishiAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });

        //刷新家居
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.JYJ_CHANNEL, 1, null, null, new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                if(jiajuAdapter != null){
                    jiajuAdapter.setData(goodList);
                    jiajuAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });

        //刷新运动
        selectGoodPresent.getSelectedGood(SelectedGoodPresenter.KDC_CHANNEL, 1, null, null, new SelectedGoodPresenter.SelectGoodGetCallBack() {
            @Override
            public void onGetGoodSucc(List<TaoBaoItemVo> goodList, LinkedList<TbCateVo> cateList) {
                if(sportAdapter != null){
                    sportAdapter.setData(goodList);
                    sportAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetGoodFail(String msg) {

            }
        });
        isLoaded = true;
    }


    public static SelectedGoodFragment newInstance() {
        SelectedGoodFragment fragment = new SelectedGoodFragment();
        return fragment;
    }
}
