package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.text.TextDirectionHeuristicCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.UserCenterPresenter;
import cn.fuyoushuo.fqbb.view.activity.ConfigActivity;
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

    @Bind(R.id.alimama_login_icon)
    TextView loginText;

    @Bind(R.id.user_center_refreshview)
    SwipeRefreshLayout userCenterRefreshView;

    @Bind(R.id.user_center_points_icon)
    RelativeLayout pointsIcon;

    @Bind(R.id.logout_area)
    CardView logoutArea;

    @Bind(R.id.bind_email)
    TextView bindEmailView;

    @Bind(R.id.update_password)
    TextView updatePasswordView;

    @Bind(R.id.user_set)
    TextView userSetView;

    boolean isEmailBind = false;

    private String phoneNum = "";

    private String email = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("dddddd");

        return super.onCreateView(inflater,container,savedInstanceState);
    }

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

        RxView.clicks(logoutArea).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                          userCenterPresenter.logout();
                    }
                });

        bindEmailView.setText(Html.fromHtml("绑定邮箱<font color=\"#ff0000\">(强烈建议,方便找回账号)</font>"));

        RxView.clicks(bindEmailView).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/9 绑定邮箱逻辑
                        if(!isEmailBind){
                          BindEmailDialogFragment.newInstance().show(getFragmentManager(),"BindEmailDialogFragment");
                        }else{
                          UnbindEmailDialogFragment.newInstance(phoneNum,email).show(getFragmentManager(),"UnbindEmailDialogFragment");
                        }
                    }
                });

        RxView.clicks(updatePasswordView).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/9 修改密码逻辑
                        UpdatePasswordDialogFragment.newInstance().show(getFragmentManager(),"UpdatePasswordDialogFragment");
                    }
                });

        RxView.clicks(userSetView).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/11/9 用户设置逻辑
                        Intent intent = new Intent(getActivity(), ConfigActivity.class);
                        startActivity(intent);
                    }
                });

        initIconFront();
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

    //初始化字体图标
    private void initIconFront() {
        Typeface iconfont = Typeface.createFromAsset(getActivity().getAssets(), "iconfront/iconfont_alimamalogin.ttf");
        loginText.setTypeface(iconfont);
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
         String email = "";
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
         if(result.containsKey("email")){
              email = result.getString("email");
         }
         if(!TextUtils.isEmpty(email)){
             bindEmailView.setText(Html.fromHtml("<font color=\"#ff0000\">解绑邮箱</font>"));
             this.email = email;
             isEmailBind = true;
         }else{
             bindEmailView.setText(Html.fromHtml("绑定邮箱<font color=\"#ff0000\">(强烈建议,方便找回账号)</font>"));
             isEmailBind = false;
             this.email = "";
         }
         this.phoneNum = account;
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
        //Toast.makeText(MyApplication.getContext(),"请稍后重新登录阿里妈妈",Toast.LENGTH_SHORT).show();
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

    @Override
    public void onLogoutSuccess() {
        Toast.makeText(MyApplication.getContext(),"退出登录成功",Toast.LENGTH_SHORT).show();
        RxBus.getInstance().send(new LogoutToMainEvent());
    }

    @Override
    public void onLogoutFail() {
        Toast.makeText(MyApplication.getContext(),"退出登录失败,请重试",Toast.LENGTH_SHORT).show();
    }

    //-----------------------------总线事件----------------------------------------------------

    public class LogoutToMainEvent extends RxBus.BusEvent{}
}
