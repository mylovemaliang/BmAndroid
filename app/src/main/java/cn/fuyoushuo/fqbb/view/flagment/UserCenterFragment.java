package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.UserCenterPresenter;
import cn.fuyoushuo.fqbb.view.activity.PointMallActivity;
import cn.fuyoushuo.fqbb.view.activity.UserLoginActivity;
import cn.fuyoushuo.fqbb.view.flagment.pointsmall.PhoneRechargeDialogFragment;
import cn.fuyoushuo.fqbb.view.view.UserCenterView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/10/27.
 */
public class UserCenterFragment extends BaseFragment implements UserCenterView{


    private UserCenterPresenter userCenterPresenter;

    @Bind(R.id.user_center_account)
    TextView accountView;

    @Bind(R.id.userinfo_currentpoints_value)
    TextView currentPoints;

    @Bind(R.id.userinfo_freezepoints_value)
    TextView freezePoints;

    @Bind(R.id.userinfo_useablepoints_value)
    TextView useablePoints;

    @Bind(R.id.userinfo_month_20day_value)
    TextView thisMonth20Count;

    @Bind(R.id.userinfo_nextmonth_20day_value)
    TextView nextMonth20Count;

    @Bind(R.id.userinfo_useable_money_value)
    TextView useableCount;

    @Bind(R.id.user_center_alimama_login)
    View alimamaLogin;

    @Bind(R.id.user_center_refreshview)
    SwipeRefreshLayout userCenterRefreshView;

    @Bind(R.id.user_center_points_icon)
    RelativeLayout pointsIcon;

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_user_center;
    }

    @Override
    protected void initView() {

        RxView.clicks(alimamaLogin).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        AlimamaLoginDialogFragment.newInstance(AlimamaLoginDialogFragment.FROM_USER_CENTER)
                                .show(getFragmentManager(),"AlimamaLoginDialogFragment");
                    }
                });

        userCenterRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                 userCenterRefreshView.setRefreshing(true);
                 refreshUserInfo();

            }
        });

        RxView.clicks(pointsIcon).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         Intent intent = new Intent(mactivity, PointMallActivity.class);
                         startActivity(intent);
                    }
                });

    }

    @Override
    protected void initData() {
      userCenterPresenter = new UserCenterPresenter(this);
    }

    public static UserCenterFragment newInstance() {
        UserCenterFragment fragment = new UserCenterFragment();
        return fragment;
    }

    //----------------------------------用于外部调用--------------------------------------------------

    public void refreshUserInfo(){
        userCenterPresenter.getUserInfo();
        userCenterPresenter.getAlimamaInfo();
    }


   //------------------------------------view 层回调--------------------------------------------------

    @Override
    public void onUserInfoGetError() {
        Toast.makeText(MyApplication.getContext(),"获取用户信息失败",Toast.LENGTH_SHORT).show();
        currentPoints.setText("--");
        freezePoints.setText("--");
        useablePoints.setText("--");
        accountView.setText("--");
        userCenterRefreshView.setRefreshing(false);
    }

    @Override
    public void onUserInfoGetSucc(JSONObject result) {
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
         userCenterRefreshView.setRefreshing(false);
    }

    @Override
    public void onLoginFail() {
        Intent intent = new Intent(mactivity, UserLoginActivity.class);
        intent.putExtra("fromWhere","UserCenter");
        startActivity(intent);
    }

    @Override
    public void onAlimamaLoginFail() {
        Toast.makeText(MyApplication.getContext(),"请稍后重新登录阿里妈妈",Toast.LENGTH_SHORT).show();
        alimamaLogin.setVisibility(View.VISIBLE);
        thisMonth20Count.setText("--");
        nextMonth20Count.setText("--");
        useableCount.setText("--");
        userCenterRefreshView.setRefreshing(false);
    }

    @Override
    public void onAlimamaLoginSuccess(JSONObject result) {
       if(result != null && !result.isEmpty()){
           if(result.containsKey("lastMonthMoney")){
               thisMonth20Count.setText(result.getString("lastMonthMoney"));
           }
           if(result.containsKey("thisMonthMoney")){
               nextMonth20Count.setText(result.getString("thisMonthMoney"));
           }
           if(result.containsKey("currentMoney")){
               useableCount.setText(result.getString("currentMoney"));
           }
       }
       alimamaLogin.setVisibility(View.GONE);
       userCenterRefreshView.setRefreshing(false);
    }

    @Override
    public void onAlimamaLoginError() {
        Toast.makeText(MyApplication.getContext(),"请稍后重新登录阿里妈妈",Toast.LENGTH_SHORT).show();
        thisMonth20Count.setText("--");
        nextMonth20Count.setText("--");
        useableCount.setText("--");
        userCenterRefreshView.setRefreshing(false);
    }
}
