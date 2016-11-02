package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding.view.RxView;

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
public class AlimamaLoginDialogFragment extends DialogFragment{


    public static AlimamaLoginDialogFragment newInstance(){
        AlimamaLoginDialogFragment adf = new AlimamaLoginDialogFragment();
        return adf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.view_superfan_page,container);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        RxView.clicks(backArea).throttleFirst(1000, TimeUnit.MILLISECONDS)
//                .subscribe(new Action1<Void>() {
//                    @Override
//                    public void call(Void aVoid) {
//                         dismissAllowingStateLoss();
//          }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mainPresenter.onDestroy();
    }


    //---------------------------------实现VIEW层的接口----------------------------------------------------


}
