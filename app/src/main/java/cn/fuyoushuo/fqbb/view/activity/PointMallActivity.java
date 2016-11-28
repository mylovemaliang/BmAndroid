package cn.fuyoushuo.fqbb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;

import org.apache.log4j.chainsaw.Main;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.view.flagment.pointsmall.DuihuanjiluDialogFragment;
import cn.fuyoushuo.fqbb.view.flagment.pointsmall.PhoneRechargeDialogFragment;
import cn.fuyoushuo.fqbb.view.flagment.pointsmall.PointsDetailDialogFragment;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by QA on 2016/11/7.
 */
public class PointMallActivity extends BaseActivity{

    @Bind(R.id.user_center_account)
    TextView accountView;

    @Bind(R.id.userinfo_currentpoints_value)
    TextView currentPoints;

    @Bind(R.id.userinfo_freezepoints_value)
    TextView freezePoints;

    @Bind(R.id.userinfo_useablepoints_value)
    TextView useablePoints;

    @Bind(R.id.points_mall_backArea)
    RelativeLayout backArea;

    @Bind(R.id.points_mall_phone_recharge)
    RelativeLayout phoneRechargeArea;

    @Bind(R.id.points_mall_tixian)
    RelativeLayout tixianArea;

    @Bind(R.id.points_mall_duihuanjilu)
    RelativeLayout duihuanArea;

    @Bind(R.id.points_mall_pointsDetail)
    RelativeLayout pointsDetailArea;

    private LocalLoginPresent localLoginPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.points_mall);
        localLoginPresent = new LocalLoginPresent();
        initView();
    }

    //初始化view
    private void initView(){

         //返回键响应
         RxView.clicks(backArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                 .subscribe(new Action1<Void>() {
                     @Override
                     public void call(Void aVoid) {
                         Intent intent = new Intent(PointMallActivity.this, MainActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         startActivity(intent);
                         finish();
                     }
                 });

        //手机充值
        RxView.clicks(phoneRechargeArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        PhoneRechargeDialogFragment.newInstance().show(getSupportFragmentManager(), "phoneRechargeDialogFragment");
                    }
                });

        //提现
        RxView.clicks(tixianArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
                    }
                });
        //兑换记录
        RxView.clicks(duihuanArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
                        DuihuanjiluDialogFragment.newInstance().show(getSupportFragmentManager(),"DuihuanjiluDialogFragment");
                    }
                });
        //积分明细
        RxView.clicks(pointsDetailArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
                        PointsDetailDialogFragment.newInstance().show(getSupportFragmentManager(),"PointsDetailDialogFragment");
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //加载用户信息
        localLoginPresent.getUserInfo(new LocalLoginPresent.UserInfoCallBack() {
            @Override
            public void onUserInfoGetSucc(JSONObject result) {
                initUserInfo(result);
            }

            @Override
            public void onUserInfoGetError() {
                currentPoints.setText("--");
                freezePoints.setText("--");
                useablePoints.setText("--");
                accountView.setText("--");
                Toast.makeText(MyApplication.getContext(),"获取用户信息错误",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localLoginPresent.onDestroy();
    }

    //初始化个人信息
    private void initUserInfo(JSONObject result){
        if(result == null || result.isEmpty()) return;
        Float validPoint = 0f;
        Float orderFreezePoint = 0f;
        Float convertFreezePoint = 0f;
        String account = "";
        if(result.containsKey("validPoint")){
            validPoint = DateUtils.getFormatFloat(result.getFloatValue("validPoint"));
        }
        if(result.containsKey("orderFreezePoint")){
            orderFreezePoint = DateUtils.getFormatFloat(result.getFloatValue("orderFreezePoint"));
        }
        if(result.containsKey("convertFreezePoint")){
            convertFreezePoint = DateUtils.getFormatFloat(result.getFloatValue("convertFreezePoint"));
        }
        if(result.containsKey("account")){
            account = result.getString("account");
        }
        currentPoints.setText(String.valueOf(validPoint+orderFreezePoint+convertFreezePoint));
        freezePoints.setText(String.valueOf(orderFreezePoint+convertFreezePoint));
        useablePoints.setText(String.valueOf(validPoint));
        accountView.setText(account);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("积分商城-主页面");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("积分商城-主页面");
    }
}
