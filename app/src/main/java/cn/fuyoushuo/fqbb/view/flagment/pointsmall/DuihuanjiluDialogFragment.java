package cn.fuyoushuo.fqbb.view.flagment.pointsmall;

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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.domain.entity.DuihuanItem;
import cn.fuyoushuo.fqbb.presenter.impl.pointsmall.DuihuanjiluPresent;
import cn.fuyoushuo.fqbb.view.Layout.DhOrderDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.adapter.DuihuanOrderAdapter;
import cn.fuyoushuo.fqbb.view.view.pointsmall.DuihuanjiluView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/7.
 */
public class DuihuanjiluDialogFragment extends RxDialogFragment implements DuihuanjiluView{


    @Bind(R.id.duihuanjilu_backArea)
    RelativeLayout backArea;

    //全部记录
    @Bind(R.id.dhjl_allRecord_button)
    Button allRecordButton;

    //正在审核
    @Bind(R.id.dhjl_underReview_button)
    Button underReviewButton;

    //已兑换
    @Bind(R.id.dhjl_exchanged_button)
    Button exchangedButton;

    //审核失败
    @Bind(R.id.dhjl_reviewFailed_button)
    Button reviewFailedButton;

    //刷新结果页
    @Bind(R.id.dhjl_result_refreshView)
    RefreshLayout resultRefreshView;

    //结果页
    @Bind(R.id.dhjl_result_rview)
    RecyclerView resultRview;

    DuihuanOrderAdapter duihuanOrderAdapter;

    DuihuanjiluPresent duihuanjiluPresent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        duihuanjiluPresent = new DuihuanjiluPresent(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pointsmall_duihuanjilu_dialog, container, false);
        ButterKnife.bind(this,inflate);
        duihuanOrderAdapter = new DuihuanOrderAdapter();
        resultRview.setHasFixedSize(true);
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(),1);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        resultRview.setLayoutManager(gridLayoutManager);
        resultRview.setAdapter(duihuanOrderAdapter);
        resultRview.addItemDecoration(new DhOrderDecoration());
        return inflate;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(backArea).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         dismissAllowingStateLoss();
                    }
                });


        resultRefreshView.setOnLoadListener(new RefreshLayout.OnLoadListener() {
            @Override
            public void onLoad() {
                Integer status = duihuanOrderAdapter.getQueryStatus();
                Integer page = duihuanOrderAdapter.getCurrentPage();
                duihuanjiluPresent.getDhOrders(status, page + 1, false);
            }
        });

        resultRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Integer status = duihuanOrderAdapter.getQueryStatus();
                duihuanjiluPresent.getDhOrders(status, 1, true);
                resultRefreshView.setRefreshing(false);
                return;
            }
        });

        RxView.clicks(allRecordButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                           duihuanjiluPresent.getDhOrders(null,1,true);
                    }
                });

        RxView.clicks(underReviewButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          duihuanjiluPresent.getDhOrders(2,1,true);
                    }
                });

        RxView.clicks(exchangedButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          duihuanjiluPresent.getDhOrders(3,1,true);
                    }
                });

        RxView.clicks(reviewFailedButton).compose(this.<Void>bindToLifecycle())
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          duihuanjiluPresent.getDhOrders(4,1,true);
                    }
                });

    }

    private void initButtonColor(Integer status){
        if(status == null){
            allRecordButton.setTextColor(getResources().getColor(R.color.module_11));
            underReviewButton.setTextColor(getResources().getColor(R.color.black));
            exchangedButton.setTextColor(getResources().getColor(R.color.black));
            reviewFailedButton.setTextColor(getResources().getColor(R.color.black));
        }
        else if(status == 2){
            allRecordButton.setTextColor(getResources().getColor(R.color.black));
            underReviewButton.setTextColor(getResources().getColor(R.color.module_11));
            exchangedButton.setTextColor(getResources().getColor(R.color.black));
            reviewFailedButton.setTextColor(getResources().getColor(R.color.black));
        }
        else if(status == 3){
            allRecordButton.setTextColor(getResources().getColor(R.color.black));
            underReviewButton.setTextColor(getResources().getColor(R.color.black));
            exchangedButton.setTextColor(getResources().getColor(R.color.module_11));
            reviewFailedButton.setTextColor(getResources().getColor(R.color.black));
        }
        else if(status == 4){
            allRecordButton.setTextColor(getResources().getColor(R.color.black));
            underReviewButton.setTextColor(getResources().getColor(R.color.black));
            exchangedButton.setTextColor(getResources().getColor(R.color.black));
            reviewFailedButton.setTextColor(getResources().getColor(R.color.module_11));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        duihuanjiluPresent.onDestroy();
    }


    @Override
    public void onStart() {
        super.onStart();
        duihuanjiluPresent.getDhOrders(null,1,false);
        allRecordButton.setTextColor(getResources().getColor(R.color.module_11));
    }


    public static DuihuanjiluDialogFragment newInstance() {

        DuihuanjiluDialogFragment fragment = new DuihuanjiluDialogFragment();
        return fragment;
    }
   //-----------------------------------------兑换记录-------------------------------------------------

    @Override
    public void onLoadDataFail(String msg) {
        Toast.makeText(MyApplication.getContext(),"网速稍慢",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadDataSuccess(Integer queryStatus, int page, boolean isRefresh, List<DuihuanItem> itemList) {
        if (isRefresh) {
            duihuanOrderAdapter.setData(itemList);
        } else {
            duihuanOrderAdapter.appendDataList(itemList);
        }
        duihuanOrderAdapter.setQueryStatus(queryStatus);
        duihuanOrderAdapter.setCurrentPage(page);
        duihuanOrderAdapter.notifyDataSetChanged();
        initButtonColor(queryStatus);
    }

}
