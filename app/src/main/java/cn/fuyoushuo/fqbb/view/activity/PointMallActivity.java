package cn.fuyoushuo.fqbb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by QA on 2016/11/7.
 */
public class PointMallActivity extends BaseActivity{



    private CompositeSubscription mSubscriptions;

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
        mSubscriptions = new CompositeSubscription();
        localLoginPresent = new LocalLoginPresent();
        initView();
    }

    //初始化view
    private void initView(){

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

         //返回键响应
         RxView.clicks(backArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                 .subscribe(new Action1<Void>() {
                     @Override
                     public void call(Void aVoid) {
                         // TODO: 2016/11/7
                     }
                 });

        //手机充值
        RxView.clicks(phoneRechargeArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
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
                    }
                });
        //积分明细 你
        RxView.clicks(pointsDetailArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/7
                    }
                });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
