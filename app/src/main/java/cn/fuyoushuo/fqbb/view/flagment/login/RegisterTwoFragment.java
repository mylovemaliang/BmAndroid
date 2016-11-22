package cn.fuyoushuo.fqbb.view.flagment.login;

import android.text.InputType;
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
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class RegisterTwoFragment extends BaseFragment {

    public static final String TAG_NAME = "register_one_fragment";

    private String phoneNum = "";

    private String verifiCode = "";

    @Bind(R.id.register_2_header_area)
    TextView headTitle;

    @Bind(R.id.acquire_verification_button)
    Button acquireVerifiButton;

    @Bind(R.id.commit_button)
    Button commitButton;

    @Bind(R.id.verificate_value)
    EditText verifiValue;

    private Long time = 60l;

    @Override
    protected String getPageName() {
        return "register-2";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_register_2;
    }

    @Override
    protected void initView() {
        verifiValue.setInputType(InputType.TYPE_CLASS_PHONE);
        verifiValue.setFocusable(true);
        verifiValue.setFocusableInTouchMode(true);
        verifiValue.requestFocus();
        RxView.clicks(acquireVerifiButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/10/28  获取验证码
                        timeForVerifiCode();
                    }
                });

        RxTextView.textChanges(verifiValue).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        verifiCode = charSequence.toString();
                    }
                });

        RxView.clicks(commitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        RxBus.getInstance().send(new ToRegisterThreeEvent(phoneNum,verifiCode));
                    }
                });

        verifiValue.setInputType(InputType.TYPE_CLASS_PHONE);
    }

    @Override
    protected void initData() {

    }

    private void timeForVerifiCode() {
        acquireVerifiButton.setText("获取验证码(60)");
        acquireVerifiButton.setClickable(false);
        acquireVerifiButton.setBackgroundColor(getResources().getColor(R.color.gray));
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat(60)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        time--;
                        if(acquireVerifiButton == null){
                            return;
                        }
                        if (time > 0) {
                            acquireVerifiButton.setText("获取验证码(" + time + ")");
                        } else {
                            acquireVerifiButton.setClickable(true);
                            acquireVerifiButton.setBackgroundColor(getResources().getColor(R.color.module_6));
                            acquireVerifiButton.setText("重新获取验证码");
                            time = 60l;
                        }
                    }
                });
    }

    public static RegisterTwoFragment newInstance() {
        RegisterTwoFragment fragment = new RegisterTwoFragment();
        return fragment;
    }

    //刷新当前的手机号
    public void refreshPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;

    }

    @Override
    public void onStart() {
        super.onStart();
        headTitle.setText("手机号 : " + phoneNum);
        timeForVerifiCode();
    }

    //-------------------------------------与ACTIVITY 通信-----------------------------------------------------
    public class ToRegisterThreeEvent extends RxBus.BusEvent {

        private String phoneNum;

        private String verifiCode;

        public ToRegisterThreeEvent(String phoneNum, String verifiCode) {
            this.phoneNum = phoneNum;
            this.verifiCode = verifiCode;
        }

        public String getPhoneNum() {
            return phoneNum;
        }

        public void setPhoneNum(String phoneNum) {
            this.phoneNum = phoneNum;
        }

        public String getVerifiCode() {
            return verifiCode;
        }

        public void setVerifiCode(String verifiCode) {
            this.verifiCode = verifiCode;
        }
    }


}
