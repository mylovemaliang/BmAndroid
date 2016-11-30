package cn.fuyoushuo.fqbb.view.flagment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.view.Layout.GoodTipDecoration;
import cn.fuyoushuo.fqbb.view.Layout.MyGridLayoutManager;
import cn.fuyoushuo.fqbb.view.adapter.GoodDetailTipAdapter;
import rx.functions.Action1;

/**
 * Created by QA on 2016/10/27.
 */
public class GoodDetailTipDialogFragment extends RxDialogFragment{


    @Bind(R.id.good_detail_tip_rview)
    RecyclerView tipRview;

    @Bind(R.id.good_detail_tip_head2)
    TextView fanliInfoText;

    @Bind(R.id.close_area)
    FrameLayout closeArea;

    GoodDetailTipAdapter goodDetailTipAdapter;

    private String fanliText;


    public static GoodDetailTipDialogFragment newInstance(String fanliText) {
        Bundle args = new Bundle();
        GoodDetailTipDialogFragment fragment = new GoodDetailTipDialogFragment();
        if(!TextUtils.isEmpty(fanliText)){
            args.putString("fanliText",fanliText);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
             fanliText = getArguments().getString("fanliText","");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.view_good_detail_tip,container);
        ButterKnife.bind(this,inflate);

        goodDetailTipAdapter = new GoodDetailTipAdapter();
        tipRview.setHasFixedSize(true);
        final MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(getActivity(),1);
        gridLayoutManager.setSpeedFast();
        //gridLayoutManager.setSpeedSlow();
        gridLayoutManager.setAutoMeasureEnabled(true);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        tipRview.addItemDecoration(new GoodTipDecoration());
        tipRview.setLayoutManager(gridLayoutManager);
        tipRview.setAdapter(goodDetailTipAdapter);
        if(!TextUtils.isEmpty(fanliText)){
          fanliInfoText.setText(Html.fromHtml("可获得<font color=\"#ff0000\">"+fanliText+"</font>的返利"));
        }else{
          fanliInfoText.setText("该商品暂无返利");
        }
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(closeArea).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
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
        //自定义宽度
        if(getDialog() != null){
         int mScreenWidth = MyApplication.getDisplayMetrics().widthPixels;
         WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
         params.width = CommonUtils.getIntHundred(mScreenWidth*0.85f);
         params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
         getDialog().getWindow().setAttributes(params);
        }
        if(goodDetailTipAdapter != null){
            goodDetailTipAdapter.setData(GoodDetailTipAdapter.detailTips);
            goodDetailTipAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("taoBaoTipPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("taoBaoTipPage");
    }

}
