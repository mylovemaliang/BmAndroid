package cn.fuyoushuo.fqbb.view.flagment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/9.
 */
public class UnbindEmailDialogFragment extends RxDialogFragment{

    @Bind(R.id.unbind_email_backArea)
    RelativeLayout backArea;

    @Bind(R.id.account_value)
    TextView emailTextView;

    @Bind(R.id.verificate_value)
    EditText verifiTextView;

    @Bind(R.id.acquire_verification_button)
    Button verifiAcquireButton;

    @Bind(R.id.commit_button)
    Button bindCommitButton;

    private String phoneNum = "";

    private String emailValue = "";

    private String verifiCodeValue = "";

    LocalLoginPresent localLoginPresent;

    private Long time = 60l;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
        if(getArguments() != null){
            this.phoneNum = getArguments().getString("phoneNum","");
            this.emailValue = getArguments().getString("email","");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.view_unbind_email, container, false);
        setupUI(inflateView,getActivity());
        ButterKnife.bind(this,inflateView);
        return inflateView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailTextView.setText(emailValue);

        RxTextView.textChanges(verifiTextView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        verifiCodeValue = charSequence.toString();
                    }
                });

        RxView.clicks(verifiAcquireButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                            timeForVerifiCode();
                            localLoginPresent.getVerifiCode(phoneNum,"phone_change_bind_email",new LocalLoginPresent.VerifiCodeGetCallBack() {
                                @Override
                                public void onVerifiCodeGetSucc(String account) {
                                    Toast.makeText(MyApplication.getContext(),"验证码发送成功,请查收短信",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onVerifiCodeGetError(String msg) {
                                    Toast.makeText(MyApplication.getContext(),"验证码发送失败,请重试",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                });

        RxView.clicks(bindCommitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        localLoginPresent.unBindMail(phoneNum,verifiCodeValue, new LocalLoginPresent.UnBindEmailCallBack() {

                            @Override
                            public void onUnBindEmailSuccess(String account) {
                                dismissAllowingStateLoss();
                                Toast.makeText(MyApplication.getContext(),"邮箱解绑成功",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onUnBindEmailFail(String msg) {
                                verifiCodeValue = "";
                                verifiTextView.setText("");
                                Toast.makeText(MyApplication.getContext(),"邮箱解绑失败,请重试",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        RxView.clicks(backArea).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        dismissAllowingStateLoss();
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        localLoginPresent.onDestroy();
        ButterKnife.unbind(this);
    }

    public static UnbindEmailDialogFragment newInstance(String phoneNum,String email) {
        Bundle args = new Bundle();
        args.putString("phoneNum",phoneNum);
        args.putString("email",email);
        UnbindEmailDialogFragment fragment = new UnbindEmailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("unbindEmail");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("unbindEmail");
    }

    private void timeForVerifiCode() {
        verifiAcquireButton.setText("获取验证码(60)");
        verifiAcquireButton.setClickable(false);
        verifiAcquireButton.setBackgroundColor(getResources().getColor(R.color.gray));
        Observable.timer(1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat(60)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(verifiAcquireButton == null) return;
                        time--;
                        if (time > 0) {
                            if(verifiAcquireButton != null){
                               verifiAcquireButton.setText("获取验证码(" + time + ")");
                            }
                        } else {
                            if(verifiAcquireButton != null){
                              verifiAcquireButton.setClickable(true);
                              verifiAcquireButton.setBackgroundColor(getResources().getColor(R.color.module_6));
                              verifiAcquireButton.setText("重新获取验证码");
                            }
                            time = 60l;
                        }
                    }
                });
    }

    //-------------------------------------------用于处理键盘--------------------------------------------------------
    private void setupUI(View view,final Activity context) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
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
