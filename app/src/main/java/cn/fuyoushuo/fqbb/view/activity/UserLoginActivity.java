package cn.fuyoushuo.fqbb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import org.apache.log4j.chainsaw.Main;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.FindPassOneFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.FindPassTwoFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.LoginOriginFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.RegisterOneFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.RegisterThreeFragment;
import cn.fuyoushuo.fqbb.view.flagment.login.RegisterTwoFragment;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 *  用于用户登录注册.
 */
public class UserLoginActivity extends BaseActivity{


    LoginOriginFragment loginOriginFragment;

    FindPassOneFragment findPassOneFragment;

    FindPassTwoFragment findPassTwoFragment;

    RegisterOneFragment registerOneFragment;

    RegisterTwoFragment registerTwoFragment;

    RegisterThreeFragment registerThreeFragment;

    private CompositeSubscription mSubscriptions;


    private Fragment mContent;


    @Bind(R.id.login_title)
    TextView headTitle;

    @Bind(R.id.login_backArea)
    View backView;

    private String biz = "";

    InputMethodManager inputMethodManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_area);
        mSubscriptions = new CompositeSubscription();
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        initFragments();
        initBusEventListen();
        initView();
        Intent intent = getIntent();
        String biz = intent.getStringExtra("biz");
        this.biz = biz == null ? "" : biz;
    }


    private void initView(){

        RxView.clicks(backView).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (inputMethodManager.isActive()) {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        Intent intent = new Intent(UserLoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        //从MAIN页面转发而来,直接回到Main页面
                        intent.putExtra("noLoginFromMain",true);
                        startActivity(intent);
                        //关掉自己
                        finish();
                    }
                });
    }


    //初始化所有的FRAGMENT
    private void initFragments(){
        loginOriginFragment = LoginOriginFragment.newInstance();

        findPassOneFragment = FindPassOneFragment.newInstance();

        findPassTwoFragment = FindPassTwoFragment.newInstance();

        registerOneFragment = RegisterOneFragment.newInstance();

        registerTwoFragment = RegisterTwoFragment.newInstance();

        registerThreeFragment = RegisterThreeFragment.newInstance();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.login_fragment_area,loginOriginFragment,LoginOriginFragment.TAG_NAME);
//        fragmentTransaction.add(R.id.login_fragment_area,findPassOneFragment,FindPassOneFragment.TAG_NAME);
//        fragmentTransaction.add(R.id.login_fragment_area,findPassTwoFragment,FindPassTwoFragment.TAG_NAME);
//        fragmentTransaction.add(R.id.login_fragment_area,registerOneFragment,RegisterOneFragment.TAG_NAME);
//        fragmentTransaction.add(R.id.login_fragment_area,registerTwoFragment,RegisterTwoFragment.TAG_NAME);
//        fragmentTransaction.add(R.id.login_fragment_area,registerThreeFragment,RegisterThreeFragment.TAG_NAME);

        //初始化 fragment 状态
        fragmentTransaction.show(loginOriginFragment);
//        fragmentTransaction.hide(findPassOneFragment);
//        fragmentTransaction.hide(findPassTwoFragment);
//        fragmentTransaction.hide(registerOneFragment);
//        fragmentTransaction.hide(registerTwoFragment);
//        fragmentTransaction.hide(registerThreeFragment);

        mContent = loginOriginFragment;

        //提交fragment 事务
        fragmentTransaction.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
    }

    //转换flagment
    public void switchContent(Fragment from, Fragment to){
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.login_fragment_area, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
            getSupportFragmentManager().executePendingTransactions();
        }
    }

    //初始化事件总线
    private void initBusEventListen(){
        mSubscriptions.add(RxBus.getInstance().toObserverable().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<RxBus.BusEvent>() {
            @Override
            public void call(RxBus.BusEvent busEvent) {
                if(busEvent instanceof LoginOriginFragment.ToRegisterOneEvent){
                    // TODO: 2016/10/28
                    headTitle.setText("注册");
                    switchContent(mContent,registerOneFragment);
                }
                else if(busEvent instanceof LoginOriginFragment.ToFindPassOneEvent){
                    headTitle.setText("找回密码");
                    switchContent(mContent,findPassOneFragment);
                }
                else if(busEvent instanceof RegisterOneFragment.ToRegisterTwoEvent){
                    RegisterOneFragment.ToRegisterTwoEvent event = (RegisterOneFragment.ToRegisterTwoEvent) busEvent;
                    String phoneNum = event.getPhoneNum();
                    registerTwoFragment.refreshPhoneNum(phoneNum);
                    switchContent(mContent,registerTwoFragment);
                }
                else if(busEvent instanceof RegisterTwoFragment.ToRegisterThreeEvent){
                    RegisterTwoFragment.ToRegisterThreeEvent event = (RegisterTwoFragment.ToRegisterThreeEvent) busEvent;
                    String phoneNum = event.getPhoneNum();
                    String verifiCode = event.getVerifiCode();
                    registerThreeFragment.refreshView(phoneNum,verifiCode);
                    switchContent(mContent,registerThreeFragment);
                }
                else if(busEvent instanceof RegisterThreeFragment.ToLoginAfterRegisterSuccess){
                    RegisterThreeFragment.ToLoginAfterRegisterSuccess event = (RegisterThreeFragment.ToLoginAfterRegisterSuccess) busEvent;
                    String phoneNum = event.getPhoneNum();
                    loginOriginFragment.refreshAccount(phoneNum);
                    switchContent(mContent,loginOriginFragment);
                }
                else if(busEvent instanceof LoginOriginFragment.LoginSuccessEvent){
                    if("".equals(biz)){
                         Intent intent = new Intent(UserLoginActivity.this,MainActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         startActivity(intent);
                         finish();
                     }
                     else if("MainToUc".equals(biz)){
                         Intent intent = new Intent(UserLoginActivity.this,MainActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         intent.putExtra("bizCallBack","MainToUc");
                         startActivity(intent);
                         finish();
                     }
                     else if("MainToLocalOrder".equals(biz)){
                         Intent intent = new Intent(UserLoginActivity.this,MainActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         intent.putExtra("bizCallBack","MainToLocalOrder");
                         startActivity(intent);
                         finish();
                     }
                     else if("MainToJdWv".equals(biz)){
                         Intent intent = new Intent(UserLoginActivity.this,MainActivity.class);
                         intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                         intent.putExtra("bizCallBack","MainToJdWv");
                         startActivity(intent);
                         finish();
                     }
                     else if("SearchToJdWv".equals(biz)){
                          Intent intent = new Intent(UserLoginActivity.this,SearchActivity.class);
                          intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                          intent.putExtra("bizCallBack","SearchToJdWv");
                          startActivity(intent);
                          finish();
                    }
                }
                else if(busEvent instanceof FindPassOneFragment.ToFindPassTwoEvent){
                     FindPassOneFragment.ToFindPassTwoEvent event = (FindPassOneFragment.ToFindPassTwoEvent) busEvent;
                     String account = event.getAccount();
                     String verifiCode = event.getVerifidataCode();
                     findPassTwoFragment.refreshView(account,verifiCode);
                     switchContent(mContent,findPassTwoFragment);
                }
                else if(busEvent instanceof FindPassTwoFragment.ToLoginAfterFindPassSucc){
                    FindPassTwoFragment.ToLoginAfterFindPassSucc event = (FindPassTwoFragment.ToLoginAfterFindPassSucc) busEvent;
                    String account = event.getAccount();
                    loginOriginFragment.refreshAccount(account);
                    switchContent(mContent,loginOriginFragment);
                }
            }
        }));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //防止FRAGMENT 重叠
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscriptions.hasSubscriptions()){
            mSubscriptions.unsubscribe();
        }
    }
}
