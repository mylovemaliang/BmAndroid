package cn.fuyoushuo.fqbb.view.flagment.login;

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
import cn.fuyoushuo.fqbb.presenter.impl.login.FindPassTwoPresenter;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.view.login.FindPassTwoView;
import rx.functions.Action1;

/**
 * 用于管理登录页面
 * Created by QA on 2016/10/28.
 */
public class FindPassTwoFragment extends BaseFragment implements FindPassTwoView{

    public static final String TAG_NAME = "find_pass_two_fragment";

    private String accountValue = "";

    private String verifiCodeValue = "";

    private String newPassValue = "";

    @Bind(R.id.newpass_value)
    EditText newPassView;

    @Bind(R.id.commit_button)
    Button commitButton;

    FindPassTwoPresenter findPassTwoPresent;

    @Override
    protected String getPageName() {
        return "findPass-2";
    }

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_find_pass_2;
    }

    @Override
    protected void initView() {

        RxTextView.textChanges(newPassView).compose(this.<CharSequence>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribe(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                         newPassValue = charSequence.toString();
                    }
                });

        RxView.clicks(commitButton).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         findPassTwoPresent.doFindPass(accountValue,verifiCodeValue,newPassValue);
                    }
                });

    }

    @Override
    protected void initData() {
        findPassTwoPresent = new FindPassTwoPresenter(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        findPassTwoPresent.onDestroy();
    }

    public static FindPassTwoFragment newInstance() {
        FindPassTwoFragment fragment = new FindPassTwoFragment();
        return fragment;
    }

    //----------------------------------------外部调用----------------------------------------------------
    public void refreshView(String accountValue,String verifiCodeValue){
        this.accountValue = accountValue;
        this.verifiCodeValue = verifiCodeValue;
    }

    //----------------------------------------view回调----------------------------------------------------

    @Override
    public void onFindSuccess(String phoneNum) {
        Toast.makeText(MyApplication.getContext(),"找回密码成功,请重新登录",Toast.LENGTH_SHORT).show();
        RxBus.getInstance().send(new ToLoginAfterFindPassSucc(accountValue));
    }

    @Override
    public void onFindFail(String phoneNum, String msg) {
        Toast.makeText(MyApplication.getContext(),"找回密码失败,请重试",Toast.LENGTH_SHORT).show();
        RxBus.getInstance().send(new ToLoginAfterFindPassSucc(""));
    }

    //---------------------------------------总线事件定义------------------------------------------------------

    public class ToLoginAfterFindPassSucc extends RxBus.BusEvent{

        private String account;

        public ToLoginAfterFindPassSucc(String account) {
            this.account = account;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }
    }
}
