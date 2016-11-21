package cn.fuyoushuo.fqbb.view.flagment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jakewharton.rxbinding.view.RxView;
import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.RxBus;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter;
import rx.functions.Action1;

/**
 * 阿里妈妈登录页面
 * Created by QA on 2016/10/27.
 */
public class AlimamaLoginDialogFragment extends DialogFragment{


    @Bind(R.id.alimama_login_backArea)
    View backArea;

    @Bind(R.id.alimama_login_webview_area)
    ViewGroup alimamaLoginView;

    WebView myWebView;

    int fromCode;

    public static final String TAOBAOKE_LOGIN_URL = "http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion";

    //从 用户中心 过来
    public static final int FROM_USER_CENTER = 1;


    public static AlimamaLoginDialogFragment newInstance(int fromCode){
        Bundle args = new Bundle();
        args.putInt("fromCode",fromCode);
        AlimamaLoginDialogFragment adf = new AlimamaLoginDialogFragment();
        adf.setArguments(args);
        return adf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        int fromCode = getArguments().getInt("fromCode", 0);
        if(fromCode != 0){
            this.fromCode = fromCode;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.view_alimama_login,container);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //初始化本页面的webview
        myWebView = new WebView(MyApplication.getContext());
        myWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myWebView.getSettings().setDomStorageEnabled(true);

        myWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        myWebView.requestFocusFromTouch();
        myWebView.setWebChromeClient(new WebChromeClient());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            myWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(myWebView, true);

        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                 if(!TextUtils.isEmpty(url) && url.equals(TAOBAOKE_LOGIN_URL)){
                     view.loadUrl(url);
                     return true;
                 }
                 return false;
            }

            @Override
            public void onPageFinished(final WebView view, String url) {
                if(url.startsWith("http://www.alimama.com/index.htm")
                    || url.startsWith("http://media.alimama.com/account/overview.htm")
                    || url.startsWith("http://media.alimama.com/account/account.htm")){ // 已登录
                    //保存淘宝登录的COOKIE
                    TaobaoInterPresenter.saveLoginCookie(url);
                    view.stopLoading();
                    afterSaveCookies();
                }
            }
        });

        //添加webview
        alimamaLoginView.addView(myWebView);

        myWebView.loadUrl(TAOBAOKE_LOGIN_URL);

        RxView.clicks(backArea).throttleFirst(1000, TimeUnit.MILLISECONDS)
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
        if(myWebView != null){
            myWebView.removeAllViews();
            myWebView.destroy();
        }
    }

    private void afterSaveCookies(){
        if(fromCode == 0) return;
        switch (fromCode){
           case FROM_USER_CENTER :
               RxBus.getInstance().send(new AlimamaLoginToUserCenterEvent());
               dismissAllowingStateLoss();
               break;
           default:
               break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("alimamaLoginPage");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("alimamaLoginPage");
    }

    //---------------------------------实现总线事件----------------------------------------------------
    public class AlimamaLoginToUserCenterEvent extends RxBus.BusEvent{}

}
