package cn.fuyoushuo.fqbb.view.flagment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter;
import cn.fuyoushuo.fqbb.view.activity.MainActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;

/**
 *  SuperBackFlagment flagment
 */
public class MyJifenFlagment extends Fragment {

    public static final String VOLLEY_TAG_NAME = "my_jifen_flagment";

    MainActivity parentActivity;

    public WebView myjifenWebview;

    private String myJfbPageUrl = "https://awp.m.etao.com/h5/order.html?needlogin=1";

    LinearLayout reflashMyJifenLl;

    TextView myjifenTitleText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.flagment_myjifen, container, false);
        parentActivity = (MainActivity) getActivity();

        reflashMyJifenLl = (LinearLayout) view.findViewById(R.id.reflashMyJifenLl);
        reflashMyJifenLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadJifenPage();
            }
        });

        myjifenTitleText = (TextView) view.findViewById(R.id.myjifenTitleText);

        myjifenWebview = (WebView) view.findViewById(R.id.myjifenWebview);
        myjifenWebview.getSettings().setJavaScriptEnabled(true);
        myjifenWebview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myjifenWebview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myjifenWebview.getSettings().setDomStorageEnabled(true);

        myjifenWebview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myjifenWebview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        myjifenWebview.requestFocusFromTouch();
        myjifenWebview.setWebChromeClient(new WebChromeClient());

        myjifenWebview.setWebViewClient(new WebViewClient(){
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
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                Log.i("JifenFlagment InterceptRequest", "shouldInterceptRequest url=" + url);
                /*WebResourceResponse response = null;
                if (url.contains("logo")) {
                    try {
                        InputStream localCopy = getAssets().open("droidyue.png");
                        response = new WebResourceResponse("image/png", "UTF-8", localCopy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return response;*/
                /*if (url.contains("s.tbcdn.cn/s/aplus_wap.js") || url.contains("login/mlogin.js")
                        || url.contains("cj.js") || url.contains("pt2.js")
                        || url.contains("um-m.js") || url.contains("um.json")) {
                    try{
                        InputStream localCopy = parentActivity.getAssets().open("abc.js");
                        WebResourceResponse response = new WebResourceResponse("text/javascript", "UTF-8", localCopy);
                        return response;
                    }catch(Exception e){

                    }
                }*/

                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                //当前WEBVIEW 非空
                if(myjifenWebview  == null){
                    return;
                }

                Log.i("JifenWebview", url);

                if(url.startsWith("http://www.alimama.com/index.htm") || url.startsWith("http://www.alimama.com/index.htm")
                        || url.startsWith("http://media.alimama.com/account/overview.htm")
                        || url.startsWith("https://www.alimama.com/index.htm") || url.startsWith("https://www.alimama.com/index.htm")
                        || url.startsWith("https://media.alimama.com/account/overview.htm")){//已登录
                    TaobaoInterPresenter.saveLoginCookie(url);
                    myjifenTitleText.setText("集分宝");
                    myjifenWebview.loadUrl(myJfbPageUrl);
                }

                String replaceUrl = myjifenWebview.getUrl().replace("https://", "").replace("http://", "");
                if((replaceUrl.startsWith("login.taobao.com/member/login.jhtml") || replaceUrl.startsWith("login.m.taobao.com/login.htm")) && replaceUrl.contains(".etao.com") && !replaceUrl.contains("order.html")){//用户登录过了，然后退出了淘宝，那么刷新会到登录页；这个时候就需要跳到我们的阿里妈妈登录页
                    TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
                        @Override
                        public void hasLoginCallback() {//阿里妈妈已登录,只是淘宝未登录
                            myjifenWebview.loadUrl("https://login.m.taobao.com/login.htm?redirectURL=https%3A%2F%2Fawp.m.etao.com%2Fh5%2Forder.html%3Fneedlogin%3D1");
                            myjifenTitleText.setText("");
                        }

                        @Override
                        public void nologinCallback() {//阿里妈妈也没有登录
                            myjifenWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                            myjifenTitleText.setText("淘宝账户登录");
                        }

                        @Override
                        public void judgeErrorCallback() {

                        }
                    },VOLLEY_TAG_NAME);

                    /*myjifenTitleText.setText("淘宝账户登录");
                    myjifenWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);*/
                }
            }
        });

        return view;
    }

    boolean firstAccess = true;

    public void loadWebviewPage(){
        if(firstAccess){//第一次访问
            firstAccess = false;
            reloadJifenPage();
        }else{//当前页面是登录页
            String replaceUrl = myjifenWebview.getUrl().replace("https://", "").replace("http://", "");
            if(replaceUrl.startsWith("login.taobao.com/member/login") || replaceUrl.startsWith("login.m.taobao.com/login.htm")){//当前页面是登录页(比如未登录访问了集分宝页面，后面提现页面登陆过了，然后退出淘宝了)
                reloadJifenPage();
            }
        }
    }

    public void reloadJifenPage(){
        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
            @Override
            public void hasLoginCallback() {
                if(myjifenWebview != null){
                    myjifenTitleText.setText("集分宝");
                    myjifenWebview.loadUrl(myJfbPageUrl);
                }
            }

            @Override
            public void nologinCallback() {
                if(myjifenWebview != null){
                  myjifenTitleText.setText("淘宝账户登录");
                  myjifenWebview.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                }
            }

            @Override
            public void judgeErrorCallback() {

            }
        },VOLLEY_TAG_NAME);
    }

    public void clearWebview(){
        if(myjifenWebview != null){
           myjifenWebview.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    public void onDestroy() {
        TaobaoInterPresenter.cancelTagedRuquests(VOLLEY_TAG_NAME);
        super.onDestroy();
    }
}
