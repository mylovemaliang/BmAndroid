package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter;
import cn.fuyoushuo.fqbb.view.activity.HelpActivity;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;

public class TixianFlagment extends RxDialogFragment {

    public static final String VOLLEY_TAG_NAME = "my_tixian_flagment";

    MainActivity parentActivity;

    public WebView mytixianWebview;

    private String tixianPageUrl = "http://media.alimama.com/account/account.htm";

    TextView tixianTitleText;

    boolean firstAccess = true;

    LinearLayout reflashTixianLl;

    LinearLayout howToTixianLl;

    RelativeLayout closeArea;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.flagment_tixian, container, false);
        parentActivity = (MainActivity) getActivity();

        reflashTixianLl = (LinearLayout) view.findViewById(R.id.reflashTixianLl);
        reflashTixianLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadTixianPage();
            }
        });

        howToTixianLl = (LinearLayout) view.findViewById(R.id.howToTixianLl);
        howToTixianLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(parentActivity, EventIdConstants.TIXIANYEMIAN_TOP_LEFT_FAQ_BTN);
                Intent intent = new Intent(parentActivity, HelpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("howToTixian", true);
                startActivity(intent);
            }
        });

        closeArea = (RelativeLayout) view.findViewById(R.id.closeArea);
        closeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dismissAllowingStateLoss();
            }
        });

        tixianTitleText = (TextView) view.findViewById(R.id.tixianTitleText);

        mytixianWebview = (WebView) view.findViewById(R.id.tixianWebview);
        mytixianWebview.getSettings().setJavaScriptEnabled(true);
        //mytixianWebview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mytixianWebview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        mytixianWebview.getSettings().setDomStorageEnabled(true);

        mytixianWebview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        mytixianWebview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        mytixianWebview.requestFocusFromTouch();
        mytixianWebview.setWebChromeClient(new WebChromeClient());

        mytixianWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    /*if(replaceUrl.startsWith("h5.m.taobao.com/awp/core/detail.htm")
                            || replaceUrl.startsWith("item.taobao.com/item.htm")
                            || replaceUrl.startsWith("detail.m.tmall.com")
                            || replaceUrl.startsWith("detail.tmall.com/item.htm")
                            || replaceUrl.startsWith("www.taobao.com/market/ju/detail_wap.php")
                            ){//是商品详情页
                        parentActivity.showWebviewFragment(url, false, false);
                        return true;
                    }*/
                    if(replaceUrl.equals("www.alimama.com/member/login.htm")){//用户登录过了，然后退出了阿里妈妈，那么刷新会到登录页；这个时候就需要跳到我们的阿里妈妈登录页
                        mytixianWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                        tixianTitleText.setText("淘宝账户登录");
                        MobclickAgent.onEvent(parentActivity, EventIdConstants.LOGIN_OF_TIXIAN_PAGE);
                        return true;
                    }

                    return false;
                }else{
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                Log.i("TixianWebview", url);
                if(url.startsWith("http://www.alimama.com/index.htm") || url.startsWith("http://www.alimama.com/index.htm")
                        || url.startsWith("http://media.alimama.com/account/overview.htm")
                        || url.startsWith("https://www.alimama.com/index.htm") || url.startsWith("https://www.alimama.com/index.htm")
                        || url.startsWith("https://media.alimama.com/account/overview.htm")){//已登录
                    TaobaoInterPresenter.saveLoginCookie(url);
                    tixianTitleText.setText("淘宝联盟");
                    mytixianWebview.loadUrl(tixianPageUrl);
                    return;
                }
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        loadWebviewPage();
    }

    public static TixianFlagment newInstance() {
        TixianFlagment fragment = new TixianFlagment();
        return fragment;
    }

    public void loadWebviewPage(){
        if(firstAccess){//第一次访问
            firstAccess = false;
            reloadTixianPage();
        }else{//当前页面是登录页
            if(mytixianWebview.getUrl()!=null && !"".equals(mytixianWebview.getUrl().trim())){
                String replaceUrl = mytixianWebview.getUrl().replace("https://", "").replace("http://", "");
                if(replaceUrl.startsWith("login.taobao.com/member/login") || replaceUrl.contains("www.alimama.com/member/login.htm") || replaceUrl.startsWith("login.m.taobao.com/login.htm")){//当前页面是登录页(比如未登录访问了订单页面，后面提现页面登陆过了，订单页面还是显示着登录页)
                    reloadTixianPage();
                }
            }else{
                reloadTixianPage();
            }
        }
    }

    public void reloadTixianPage(){
        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
            @Override
            public void hasLoginCallback() {
                if(mytixianWebview!=null){
                    tixianTitleText.setText("淘宝联盟");
                    mytixianWebview.loadUrl(tixianPageUrl);
                }
            }

            @Override
            public void nologinCallback() {
                if(mytixianWebview!=null){
                    tixianTitleText.setText("淘宝账户登录");
                    mytixianWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                    MobclickAgent.onEvent(parentActivity, EventIdConstants.LOGIN_OF_TIXIAN_PAGE);
                }
            }

            @Override
            public void judgeErrorCallback() {

            }
        },VOLLEY_TAG_NAME);
    }

    public void clearWebview(){
        mytixianWebview.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public void onDestroy() {
        TaobaoInterPresenter.cancelTagedRuquests(VOLLEY_TAG_NAME);
        if(mytixianWebview!=null){
            ViewGroup viewGroup = (ViewGroup) mytixianWebview.getParent();
            if(viewGroup!=null){
                viewGroup.removeView(mytixianWebview);
            }
            mytixianWebview.removeAllViews();
            mytixianWebview.destroy();
        }
        super.onDestroy();
    }
}
