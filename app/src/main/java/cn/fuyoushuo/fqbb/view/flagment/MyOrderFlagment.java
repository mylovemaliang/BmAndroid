package cn.fuyoushuo.fqbb.view.flagment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;

public class MyOrderFlagment extends Fragment {

    public static final String VOLLEY_TAG_NAME = "my_order_flagment";

    MainActivity parentActivity;

    public WebView myorderWebview;

    LinearLayout reflashMyOrderLl;

    TextView myorderTitleText;

    private String myOrderUrl = "https://h5.m.taobao.com/taokeapp/report/detail.html?tab=2";

    boolean noLoginIntercept = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.flagment_myorder1, container, false);
        parentActivity = (MainActivity) getActivity();

        reflashMyOrderLl = (LinearLayout) view.findViewById(R.id.reflashMyOrderLl);
        reflashMyOrderLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadMyOrderPage();
            }
        });

        myorderTitleText = (TextView) view.findViewById(R.id.myorderTitleText);

        myorderWebview = (WebView) view.findViewById(R.id.myorderWebview);
        if(Build.VERSION.SDK_INT >= 21){
            myorderWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        myorderWebview.getSettings().setJavaScriptEnabled(true);
        //myorderWebview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myorderWebview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myorderWebview.getSettings().setDomStorageEnabled(true);

        myorderWebview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myorderWebview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        myorderWebview.requestFocusFromTouch();
        myorderWebview.setWebChromeClient(new WebChromeClient());

        myorderWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    if(replaceUrl.startsWith("h5.m.taobao.com/awp/core/detail.htm")
                            || replaceUrl.startsWith("item.taobao.com/item.htm")
                            || replaceUrl.startsWith("detail.m.tmall.com")
                            || replaceUrl.startsWith("detail.tmall.com/item.htm")
                            || replaceUrl.startsWith("www.taobao.com/market/ju/detail_wap.php")
                            ){//是商品详情页
                        Intent intent = new Intent(getActivity(), WebviewActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent.putExtra("loadUrl", url);
                        intent.putExtra("forSearchGoodInfo", false);
                        startActivity(intent);
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

                if(myorderWebview == null){
                    return;
                }

                Log.i("OrderWebview", url);

                if(url.startsWith("http://www.alimama.com/index.htm") || url.startsWith("http://www.alimama.com/index.htm")
                        || url.startsWith("http://media.alimama.com/account/overview.htm")
                        || url.startsWith("https://www.alimama.com/index.htm") || url.startsWith("https://www.alimama.com/index.htm")
                        || url.startsWith("https://media.alimama.com/account/overview.htm")){//已登录
                    TaobaoInterPresenter.saveLoginCookie(url);
                    myorderTitleText.setText("查询订单");
                    myorderWebview.loadUrl(myOrderUrl);
                }

                String replaceUrl = myorderWebview.getUrl().replace("https://", "").replace("http://", "");
                if(replaceUrl.equals("login.m.taobao.com/login.htm")){//用户登录过了，然后退出了淘宝，那么刷新会到登录页；这个时候就需要跳到我们的阿里妈妈登录页
                    TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
                        @Override
                        public void hasLoginCallback() {//阿里妈妈已登录,只是淘宝未登录
                            myorderWebview.loadUrl("https://login.m.taobao.com/login.htm?redirectURL=https%3A%2F%2Fh5.m.taobao.com%2Ftaokeapp%2Freport%2Fdetail.html%3Ftab%3D2");
                            myorderTitleText.setText("");
                        }

                        @Override
                        public void nologinCallback() {//阿里妈妈也没有登录
                            myorderWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                            myorderTitleText.setText("淘宝账户登录");
                            MobclickAgent.onEvent(parentActivity, EventIdConstants.LOGIN_OF_ORDER_PAGE);
                        }

                        @Override
                        public void judgeErrorCallback() {

                        }
                    },VOLLEY_TAG_NAME);
                }
            }
        });

        return view;
    }

    boolean firstAccess = true;
    public void loadWebviewPage(){
        if(myorderWebview == null){
            return;
        }
        if(firstAccess){//第一次访问
            firstAccess = false;
            reloadMyOrderPage();
        }else{
            if(myorderWebview.getUrl()!=null && !"".equals(myorderWebview.getUrl().trim())){
                String replaceUrl = myorderWebview.getUrl().replace("https://", "").replace("http://", "");
                if(replaceUrl.startsWith("login.taobao.com/member/login") || replaceUrl.contains("www.alimama.com/member/login.htm") || replaceUrl.startsWith("login.m.taobao.com/login.htm")){//当前页面是我们的阿里妈妈登录页(比如未登录访问了订单页面，后面提现页面登陆过了，订单页面还是显示着登录页)
                    reloadMyOrderPage();
                }
            }else{
                reloadMyOrderPage();
            }
        }
    }

    public void reloadMyOrderPage(){
        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
            @Override
            public void hasLoginCallback() {
                if(myorderWebview!=null){
                    myorderTitleText.setText("查询订单");
                    myorderWebview.loadUrl(myOrderUrl);
                }
            }

            @Override
            public void nologinCallback() {
                if(myorderWebview!=null){
                    myorderTitleText.setText("淘宝账户登录");
                    myorderWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                    MobclickAgent.onEvent(parentActivity, EventIdConstants.LOGIN_OF_ORDER_PAGE);
                }
            }

            @Override
            public void judgeErrorCallback() {

            }
        },VOLLEY_TAG_NAME);
    }

    public void clearWebview(){
        if(myorderWebview != null) {
            myorderWebview.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    public void onDestroy() {
        //当前 flagment 销毁时,取消所有进行及等待的请求
        TaobaoInterPresenter.cancelTagedRuquests(VOLLEY_TAG_NAME);
        super.onDestroy();
    }
}
