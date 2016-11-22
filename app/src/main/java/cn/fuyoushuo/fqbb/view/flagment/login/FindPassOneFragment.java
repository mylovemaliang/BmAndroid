package cn.fuyoushuo.fqbb.view.flagment.login;

import android.text.InputType;
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
import cn.fuyoushuo.fqbb.commonlib.utils.DataCheckUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class FindPassOneFragment extends BaseFragment {

    public static final String TAG_NAME = "find_pass_one_fragment";

    private String accountValue = "";

    private String verifiCodeValue = "";

    @Bind(R.id.account_value)
    EditText accountView;

    @Bind(R.id.verificate_value)
    EditText verifiCodeView;

    @Bind(R.id.acquire_verification_button)
    Button sendVerificodeButton;

    @Bind(R.id.commit_button)
    Button commitButton;

    private Long time = 60l;

    LocalLoginPresent localLoginPresent;


    @Override
    protected String getPageName() {
        return "findPass_1";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_find_pass_1;
    }

    @Override
    protected void initView() {
        accountView.setFocusable(true);
        accountView.setFocusableInTouchMode(true);
        accountView.requestFocus();
        accountView.setInputType(InputType.TYPE_CLASS_PHONE);
        verifiCodeView.setInputType(InputType.TYPE_CLASS_PHONE);

        RxView.clicks(sendVerificodeButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        // TODO: 2016/10/28  获取验证码
                        if(!DataCheckUtils.isChinaPhoneLegal(accountValue)){
                            accountView.setText("");
                            accountValue = "";
                            Toast.makeText(MyApplication.getContext(),"手机号码格式不对,请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        localLoginPresent.getVerifiCode(accountValue, "phone_find_pwd", new LocalLoginPresent.VerifiCodeGetCallBack() {
                            @Override
                            public void onVerifiCodeGetSucc(String phoneNum) {
                                Toast.makeText(MyApplication.getContext(),"验证码已发送,请注意查收",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onVerifiCodeGetError(String msg) {
                                Toast.makeText(MyApplication.getContext(),msg,Toast.LENGTH_SHORT).show();
                            }
                        });
                        timeForVerifiCode();
                    }
                });

        RxTextView.textChanges(verifiCodeView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        verifiCodeValue = charSequence.toString();
                    }
                });

        RxTextView.textChanges(accountView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        accountValue = charSequence.toString();
                    }
                });

        RxView.clicks(commitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(!DataCheckUtils.isChinaPhoneLegal(accountValue)){
                            accountView.setText("");
                            accountValue = "";
                            Toast.makeText(MyApplication.getContext(),"手机号码格式不对,请重新输入",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                           RxBus.getInstance().send(new ToFindPassTwoEvent(accountValue,verifiCodeValue));
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        localLoginPresent = new LocalLoginPresent();
    }


    private void timeForVerifiCode() {
        sendVerificodeButton.setText("获取验证码(60)");
        sendVerificodeButton.setClickable(false);
        sendVerificodeButton.setBackgroundColor(getResources().getColor(R.color.gray));
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat(60)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(sendVerificodeButton == null){
                           return;
                        }
                        time--;
                        if (time > 0) {
                            sendVerificodeButton.setText("获取验证码(" + time + ")");
                        } else {
                            sendVerificodeButton.setClickable(true);
                            sendVerificodeButton.setBackgroundColor(getResources().getColor(R.color.module_6));
                            sendVerificodeButton.setText("重新获取验证码");
                            time = 60l;
                        }
                    }
                });
    }


    public static FindPassOneFragment newInstance() {
        FindPassOneFragment fragment = new FindPassOneFragment();
        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localLoginPresent.onDestroy();
    }

    //------------------------------------定义总线事件--------------------------------------

    public class ToFindPassTwoEvent extends RxBus.BusEvent{

        private String account;

        private String verifidataCode;

        public ToFindPassTwoEvent(String account, String verifidataCode) {
            this.account = account;
            this.verifidataCode = verifidataCode;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getVerifidataCode() {
            return verifidataCode;
        }

        public void setVerifidataCode(String verifidataCode) {
            this.verifidataCode = verifidataCode;
        }
    }

}
