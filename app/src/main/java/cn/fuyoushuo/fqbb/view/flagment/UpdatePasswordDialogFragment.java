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
public class UpdatePasswordDialogFragment extends RxDialogFragment{

    @Bind(R.id.update_password_backArea)
    RelativeLayout backArea;

    @Bind(R.id.origin_password_value)
    EditText originPasswordView;

    @Bind(R.id.password_value)
    EditText currentPasswordView;

    @Bind(R.id.commit_button)
    Button bindCommitButton;

    private String originPasswordValue = "";

    private String currentPasswordValue = "";

    LocalLoginPresent localLoginPresent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflateView = inflater.inflate(R.layout.view_update_password, container, false);
        setupUI(inflateView,getActivity());
        ButterKnife.bind(this,inflateView);
        return inflateView;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxTextView.textChanges(originPasswordView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        originPasswordValue = charSequence.toString();
                    }
                });

        RxTextView.textChanges(currentPasswordView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        currentPasswordValue = charSequence.toString();
                    }
                });

        RxView.clicks(bindCommitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         //提交变更
                         if(TextUtils.isEmpty(originPasswordValue) || TextUtils.isEmpty(currentPasswordValue)){
                               Toast.makeText(MyApplication.getContext(),"请完善修改信息",Toast.LENGTH_SHORT).show();
                         }else{
                             localLoginPresent.updatePassword(originPasswordValue, currentPasswordValue, new LocalLoginPresent.UpdatePasswordCallBack() {
                                 @Override
                                 public void onUpdatePasswordSucc() {
                                     Toast.makeText(MyApplication.getContext(),"密码更改成功",Toast.LENGTH_SHORT).show();
                                     dismissAllowingStateLoss();
                                 }

                                 @Override
                                 public void onUpdatePasswordFail() {
                                     originPasswordView.setText("");
                                     currentPasswordView.setText("");
                                     Toast.makeText(MyApplication.getContext(),"密码更改失败,请重试",Toast.LENGTH_SHORT).show();
                                 }
                             });
                         }
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

    public static UpdatePasswordDialogFragment newInstance() {
        UpdatePasswordDialogFragment fragment = new UpdatePasswordDialogFragment();
        return fragment;
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
