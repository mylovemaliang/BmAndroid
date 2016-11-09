package cn.fuyoushuo.fqbb.view.flagment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxDialogFragment;

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
public class BindEmailDialogFragment extends RxDialogFragment{


     @Bind(R.id.bind_email_backArea)
     RelativeLayout backArea;

     @Bind(R.id.account_value)
     EditText emailTextView;

     @Bind(R.id.verificate_value)
     EditText verifiTextView;

     @Bind(R.id.acquire_verification_button)
     Button verifiAcquireButton;

     @Bind(R.id.commit_button)
     Button bindCommitButton;

     private String emailValue = "";

     private String verifiCodeValue = "";

     LocalLoginPresent localLoginPresent;

     private boolean isAccountRight = false;

     private Long time = 60l;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.view_bind_email, container, false);
        setupUI(inflateView,getActivity());
        ButterKnife.bind(this,inflateView);
        emailTextView.setFocusable(true);
        return inflateView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxTextView.textChanges(emailTextView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        emailValue = charSequence.toString();
                    }
                });

        RxTextView.textChanges(verifiTextView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        verifiCodeValue = charSequence.toString();
                    }
                });

        RxView.focusChanges(emailTextView).compose(this.<Boolean>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(!aBoolean){
                            //判断邮箱是否有效
                            localLoginPresent.validateData(emailValue, 2, new LocalLoginPresent.DataValidataCallBack() {
                                @Override
                                public void onValidataSucc(int flag) {
                                    if(flag == 1){
                                        isAccountRight = false;
                                        Toast.makeText(MyApplication.getContext(),"邮箱已经被注册",Toast.LENGTH_SHORT).show();
                                    }else{
                                        isAccountRight = true;
                                        Toast.makeText(MyApplication.getContext(),"邮箱可以正常使用",Toast.LENGTH_SHORT).show();
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

        RxView.clicks(verifiAcquireButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(!isAccountRight){
                            Toast.makeText(MyApplication.getContext(),"账号有误,请检查账号后再次输入",Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            timeForVerifiCode();
                            localLoginPresent.getVerifiCode(emailValue, "email_bind_email", new LocalLoginPresent.VerifiCodeGetCallBack() {
                                @Override
                                public void onVerifiCodeGetSucc(String account) {
                                    Toast.makeText(MyApplication.getContext(),"验证码发送成功,请查收邮件",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onVerifiCodeGetError(String msg) {
                                    Toast.makeText(MyApplication.getContext(),"验证码发送失败,请重试",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

        RxView.clicks(bindCommitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        localLoginPresent.bindMail(emailValue, verifiCodeValue, new LocalLoginPresent.BindEmailCallBack() {
                            @Override
                            public void onBindEmailSuccess(String account) {
                                Toast.makeText(MyApplication.getContext(),"邮箱绑定成功",Toast.LENGTH_SHORT).show();
                                dismissAllowingStateLoss();
                            }

                            @Override
                            public void onBindEmailFail(String msg) {
                                emailValue = "";
                                verifiCodeValue = "";
                                emailTextView.setText("");
                                verifiTextView.setText("");
                                isAccountRight = false;
                                Toast.makeText(MyApplication.getContext(),"邮箱绑定失败,请重试",Toast.LENGTH_SHORT).show();
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

    public static BindEmailDialogFragment newInstance() {

        BindEmailDialogFragment fragment = new BindEmailDialogFragment();
        return fragment;
    }

    private void timeForVerifiCode() {
        verifiAcquireButton.setText("获取验证码(60)");
        verifiAcquireButton.setClickable(false);
        verifiAcquireButton.setBackgroundColor(getResources().getColor(R.color.gray));
        Observable.timer(1, TimeUnit.SECONDS).compose(this.<Long>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .repeat(60)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if(verifiAcquireButton == null) return;
                        time--;
                        if (time > 0) {
                            if (verifiAcquireButton != null){
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
