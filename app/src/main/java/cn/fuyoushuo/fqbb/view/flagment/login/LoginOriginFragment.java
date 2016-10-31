package cn.fuyoushuo.fqbb.view.flagment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import rx.functions.Action1;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class LoginOriginFragment extends BaseFragment {

    public static final String TAG_NAME = "login_origin_fragment";

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

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_login_origin;
    }

    @Override
    protected void initView() {
        //点击去注册页面
        RxView.clicks(quickRegister).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         RxBus.getInstance().send(new ToRegisterOneEvent());
                    }
                });
    }

    @Override
    protected void initData() {

    }


    public static LoginOriginFragment newInstance() {
        LoginOriginFragment fragment = new LoginOriginFragment();
        return fragment;
    }


    public void refreshAccount(String phoneNum){
           account.setText(phoneNum);
    }


  //--------------------------------------与 Activity 通信-------------------------------------------------

    public class ToRegisterOneEvent extends RxBus.BusEvent {}

}
