package cn.fuyoushuo.fqbb.view.flagment.login;

import android.text.TextUtils;
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
                        registerOnePresenter.getVerifiCode(phoneNum);
                    }
                });

    }

    @Override
    protected void initData() {
        registerOnePresenter = new RegisterOnePresenter(this);
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
}
