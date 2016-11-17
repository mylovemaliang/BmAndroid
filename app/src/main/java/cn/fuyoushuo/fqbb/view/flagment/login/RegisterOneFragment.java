package cn.fuyoushuo.fqbb.view.flagment.login;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.presenter.impl.login.RegisterOnePresenter;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.view.login.RegisterOneView;
import rx.functions.Action1;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class RegisterOneFragment extends BaseFragment implements RegisterOneView{

    public static final String TAG_NAME = "register_one_fragment";

    @Bind(R.id.account_value)
    EditText phoneNumText;

    @Bind(R.id.commit_button)
    Button commitButton;

    private String phoneNum = "88888888888";

    RegisterOnePresenter registerOnePresenter;

    LocalLoginPresent localLoginPresent;

    //判断账号是否满足要求
    private boolean isAccountRight = false;


    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_register_1;
    }

    @Override
    protected void initView() {
        RxTextView.textChanges(phoneNumText)
                .compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                         phoneNum = charSequence.toString();
                    }
                });


        RxView.clicks(commitButton).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(isAccountRight){
                            registerOnePresenter.getVerifiCode(phoneNum);
                        }else{
                            Toast.makeText(MyApplication.getContext(),"手机号码不可用,请检查账号后再次输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });


        RxView.focusChanges(phoneNumText).compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(!aBoolean){
                            //判断邮箱是否有效
                            localLoginPresent.validateData(phoneNum,1, new LocalLoginPresent.DataValidataCallBack() {
                                @Override
                                public void onValidataSucc(int flag) {
                                    if(flag == 1){
                                        isAccountRight = false;
                                        Toast.makeText(MyApplication.getContext(),"手机号码已被注册",Toast.LENGTH_SHORT).show();
                                    }else{
                                        isAccountRight = true;
                                        Toast.makeText(MyApplication.getContext(),"手机号码可以正常使用",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onValidataFail(String msg) {
                                    isAccountRight = false;
                                    Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

    }

    @Override
    protected void initData() {
        registerOnePresenter = new RegisterOnePresenter(this);
        localLoginPresent = new LocalLoginPresent();
    }


    public static RegisterOneFragment newInstance() {
        RegisterOneFragment fragment = new RegisterOneFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registerOnePresenter != null){
            registerOnePresenter.onDestroy();
        }
        if(localLoginPresent != null){
            localLoginPresent.onDestroy();
        }
    }


    //---------------------------------实现VIEW层的接口---------------------------------------------------------

    @Override
    public void onErrorRecieveVerifiCode(String respMsg) {
        String msg = "验证码发送失败,请重试";
        if(!TextUtils.isEmpty(respMsg)){
             msg = respMsg;
        }
        Toast.makeText(MyApplication.getContext(),respMsg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessRecieveVerifiCode(String phoneNum) {
        Toast.makeText(MyApplication.getContext(),"验证码发送成功,请耐心等待",Toast.LENGTH_SHORT).show();
        RxBus.getInstance().send(new ToRegisterTwoEvent(phoneNum));
    }

    //--------------------------------与activity 通信接口------------------------------------------------------------
    public class ToRegisterTwoEvent extends RxBus.BusEvent{

        private String phoneNum;

        public ToRegisterTwoEvent(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }
    }

    //--------------------------------焦点重定向----------------------------------------------

    @Override
    protected void setupUI(View view, final Activity context) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    v.setFocusable(true);
                    v.setFocusableInTouchMode(true);
                    v.requestFocus();
                    InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    return false;
                }
            });

        }
        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView,context);
            }
        }
    }
}
