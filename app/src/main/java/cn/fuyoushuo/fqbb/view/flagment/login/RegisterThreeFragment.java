package cn.fuyoushuo.fqbb.view.flagment.login;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.login.RegisterThreePresenter;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.view.login.RegisterThreeView;
import rx.functions.Action1;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class RegisterThreeFragment extends BaseFragment implements RegisterThreeView{


    public static final String TAG_NAME = "register_one_fragment";

    private String phoneNum = "";

    private String verifiCode = "";

    private String password = "";

    @Bind(R.id.register_3_header_area)
    TextView headerTitle;

    @Bind(R.id.password_value)
    EditText passwordView;

    @Bind(R.id.commit_button)
    Button commitButton;

    RegisterThreePresenter registerThreePresenter;

    @Override
    protected String getPageName() {
        return "register-3";
    }

    @Override
    protected int getRootLayoutId() {
            return R.layout.fragment_register_3;
    }

    @Override
    protected void initView() {
        passwordView.setFocusable(true);
        passwordView.setFocusableInTouchMode(true);
        passwordView.requestFocus();

        RxTextView.textChanges(passwordView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                          password = charSequence.toString();
                    }
        });

        RxView.clicks(commitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW)).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/10/28  提交注册信息之后
                        registerThreePresenter.registUser(phoneNum,password,verifiCode);
                    }
                });


    }

    @Override
    protected void initData() {
        registerThreePresenter = new RegisterThreePresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerThreePresenter.onDestroy();
    }

    public static RegisterThreeFragment newInstance() {
        RegisterThreeFragment fragment = new RegisterThreeFragment();
        return fragment;
    }

    public void refreshView(String phoneNum,String verifiCode){
        this.phoneNum = phoneNum;
        this.verifiCode = verifiCode;
    }

    @Override
    public void onStart() {
        super.onStart();
        headerTitle.setText("手机号 : "+phoneNum);
    }

    //--------------------------------------------实现　view　层接口----------------------------------
   //当注册成功后的逻辑　
    @Override
    public void onRegistSuccess(String phoneNum) {
        RxBus.getInstance().send(new ToLoginAfterRegisterSuccess(phoneNum));
    }

    //当注册失败后的逻辑
    @Override
    public void onRegistFail(String phoneNum, String msg) {

    }


   //----------------------------------------总线EVENT定义------------------------------------------

    public class ToLoginAfterRegisterSuccess extends RxBus.BusEvent{

        private String phoneNum;

        public ToLoginAfterRegisterSuccess(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getPhoneNum() {
            return phoneNum;
        }
    }
}
