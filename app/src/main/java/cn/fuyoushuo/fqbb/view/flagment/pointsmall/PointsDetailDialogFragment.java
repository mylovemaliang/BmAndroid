package cn.fuyoushuo.fqbb.view.flagment.pointsmall;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import cn.fuyoushuo.fqbb.domain.entity.DuihuanDetail;
import cn.fuyoushuo.fqbb.presenter.impl.pointsmall.DuihuanDetailPresent;
import cn.fuyoushuo.fqbb.view.Layout.DhOrderDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.Layout.RefreshLayout;
import cn.fuyoushuo.fqbb.view.adapter.DuihuanDetailAdapter;
import cn.fuyoushuo.fqbb.view.view.pointsmall.DuihuanDetailView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/7.
 */
public class PointsDetailDialogFragment extends RxDialogFragment implements DuihuanDetailView{


    @Bind(R.id.dh_detail_backArea)
    RelativeLayout backArea;

    //刷新结果页
    @Bind(R.id.dh_detail_refreshView)
    RefreshLayout resultRefreshView;

    //结果页
    @Bind(R.id.dh_detail_rview)
    RecyclerView resultRview;

    DuihuanDetailAdapter duihuanDetailAdapter;

    DuihuanDetailPresent duihuanDetailPresent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        duihuanDetailPresent = new DuihuanDetailPresent(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_pointsmall_pointsdetail_dialog, container, false);
        ButterKnife.bind(this,inflate);
        duihuanDetailAdapter = new DuihuanDetailAdapter();
        resultRview.setHasFixedSize(true);
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(),1);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        resultRview.setLayoutManager(gridLayoutManager);
        resultRview.setAdapter(duihuanDetailAdapter);
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
                Integer page = duihuanDetailAdapter.getCurrentPage();
                duihuanDetailPresent.getDhDetails(page + 1,false);
            }
        });

        resultRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                duihuanDetailPresent.getDhDetails(1, true);
                resultRefreshView.setRefreshing(false);
                return;
            }
        });

        duihuanDetailPresent.getDhDetails(1,false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(duihuanDetailPresent != null){
            duihuanDetailPresent.onDestroy();
        }
    }

    public static PointsDetailDialogFragment newInstance() {

        PointsDetailDialogFragment fragment = new PointsDetailDialogFragment();
        return fragment;
    }


    //--------------------------------------回调VIEW接口----------------------------------------------

    @Override
    public void onLoadDataFail(String msg) {
        Toast.makeText(MyApplication.getContext(),"网速稍慢",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoadDataSucc(int pageNum, boolean isRefresh, List<DuihuanDetail> details) {
        if (isRefresh) {
            duihuanDetailAdapter.setData(details);
        } else {
            duihuanDetailAdapter.appendDataList(details);
        }
        duihuanDetailAdapter.setCurrentPage(pageNum);
        duihuanDetailAdapter.notifyDataSetChanged();
    }
}
