package cn.fuyoushuo.fqbb.view.flagment.login;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import org.apache.log4j.chainsaw.Main;
import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.login.LoginOriginPresenter;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.view.login.LoginOriginView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class LoginOriginFragment extends BaseFragment implements LoginOriginView{

    public static final String TAG_NAME = "login_origin_fragment";

    //账号信息
    private String accountValue;

    //密码信息
    private String passwordValue;

    @Bind(R.id.account_value)
    EditText account;

    @Bind(R.id.password_value)
    EditText password;

    @Bind(R.id.forget_pass_text)
    TextView passwordForgotten;

    @Bind(R.id.quick_register_text)
    TextView quickRegister;

    @Bind(R.id.login_button)
    Button loginButton;

    private LoginOriginPresenter loginOriginPresenter;

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_login_origin;
    }

    @Override
    protected void initView() {

        RxTextView.textChanges(account).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        accountValue = charSequence.toString();
                    }
                });

        RxTextView.textChanges(password).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                         passwordValue = charSequence.toString();
                    }
                });


        //点击去找回密码界面
        RxView.clicks(passwordForgotten).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        RxBus.getInstance().send(new ToFindPassOneEvent());
                    }
                });


        //点击去注册页面
        RxView.clicks(quickRegister).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         RxBus.getInstance().send(new ToRegisterOneEvent());
                    }
                });

        //点击登录
        RxView.clicks(loginButton).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        loginOriginPresenter.userLogin(accountValue,passwordValue);
                    }
                });
    }

    @Override
    protected void initData() {
          loginOriginPresenter = new LoginOriginPresenter(this);
    }


    public static LoginOriginFragment newInstance() {
        LoginOriginFragment fragment = new LoginOriginFragment();
        return fragment;
    }


    public void refreshAccount(String phoneNum){
        accountValue = phoneNum;
        account.setText(phoneNum);
    }



    //----------------------------------------回调VIEW层的接口--------------------------------------------

    @Override
    public void onLoginSuccess(String account) {
        Toast.makeText(MyApplication.getContext(),"登录成功",Toast.LENGTH_SHORT).show();
        //
        Observable.timer(1,TimeUnit.SECONDS).compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        RxBus.getInstance().send(new LoginSuccessEvent());
                    }
                });


    }

    @Override
    public void onLoginFail(String account, String msg) {
        password.setText("");
        passwordValue = "";
        Toast.makeText(MyApplication.getContext(),"登录失败,请重试",Toast.LENGTH_SHORT).show();
    }


    //--------------------------------------与 Activity 通信-------------------------------------------------

    public class ToRegisterOneEvent extends RxBus.BusEvent {}

    public class LoginSuccessEvent extends RxBus.BusEvent{}

    public class ToFindPassOneEvent extends RxBus.BusEvent{}

}
