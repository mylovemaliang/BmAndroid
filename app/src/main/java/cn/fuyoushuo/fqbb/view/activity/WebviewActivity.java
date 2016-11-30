package cn.fuyoushuo.fqbb.view.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.umeng.analytics.MobclickAgent;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.commonlib.utils.okhttp.PersistentCookieStore;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter;
import cn.fuyoushuo.fqbb.view.flagment.GoodDetailTipDialogFragment;

public class WebviewActivity extends BaseActivity {

    public static final String VOLLEY_TAG_NAME = "webview_activity";

    public WebView myWebView;

    private Button loginAlimama;//登录按钮

    private Button ktfxQx;//开通权限按钮

    private Button wsxx;//修复权限按钮

    private Button qdfx;//启动返现按钮

    private Button qdfjfb;//启动返集分宝按钮

    private Button reflashFl;  //刷新获取返利按钮（比如获取到返利了，但是获取返利链接流程节点出问题，就显示）

    private String etaoJfbUrl;

    private String loginPreUrl;

    private String ktqxPreUrl;  //开通返现权限按钮点击前的URL

    public Handler handler = new Handler();

    private Long currentItemId = 0l;

    private String currentItemUrl;

    private Integer currentItemIsGaofan = 0;  //1表示高返    0表示非高返

    private RelativeLayout webviewBottom;

    private TextView leftDetailInfo;//有红色背景的文案提示信息

    private TextView itemfxinfo1;//反金额和返集分宝的“返”字；或者提示信息（如请开通权限才能获得返钱）

    private TextView itemfxinfo2;//返现百分比的数字，或者集分宝数字

    private TextView itemfxinfo3;//约返XXX元；集分宝

    private final String tbcacheFile = "tbcache";

    private String myTaobaoPageUrl = "https://h5.m.taobao.com/mlapp/mytaobao.html#mlapp-mytaobao";

    private Long initTaobaoItemId;  //进入当前activity时的商品ID（如果是我的淘宝，那么这个值为-1），用于控制是网页后退还是关闭activity

    private LinearLayout back;

    private ImageView webviewBackImg;

    private LinearLayout webviewToHomeLl;

    private LinearLayout tipArea;

    //是否从商品搜索页转发过来
    private boolean isFromGoodSearch = false;

    TextView webviewTitleText;

    TextView webviewToHome;

    private boolean doGoBack = false;

    private String preWebViewUrl;

    private String relatedGoodUrl = "";

    //保存最初加载的URL
    private String originLoadUrl = "";

    //保存当前页面的业务类型
    private String bizString;

    public String getOriginLoadUrl() {
        return originLoadUrl;
    }

    public boolean isFromGoodSearch() {
        return isFromGoodSearch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_webview);

        webviewTitleText = (TextView) this.findViewById(R.id.webviewTitleText);
        webviewToHome = (TextView) this.findViewById(R.id.webviewToHome);

        webviewBackImg = (ImageView) this.findViewById(R.id.webviewBackImg);
        back = (LinearLayout) this.findViewById(R.id.webviewGoBackLl);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        webviewToHomeLl = (LinearLayout) this.findViewById(R.id.webviewToHomeLl);
        webviewToHomeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeWebviewActivity();
            }
        });

        webviewBottom = (RelativeLayout) this.findViewById(R.id.webviewFragBottom);

        tipArea = (LinearLayout) this.findViewById(R.id.detailBottomRmBtn);

        tipArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemfxinfo2 != null){
                    CharSequence text = itemfxinfo2.getText();
                    if(text != null) {
                        GoodDetailTipDialogFragment.newInstance(text.toString()).show(getSupportFragmentManager(), "GoodDetailTipDialogFragment");
                    }
                }
            }
        });

        myWebView = (WebView) this.findViewById(R.id.tb_h5page_webview);

        /*if(myWebView==null){
            LinearLayout webviewLl = (LinearLayout) this.findViewById(R.id.tb_h5page_webview);
            myWebView = new WebView(this.getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            myWebView.setLayoutParams(lp);
            webviewLl.addView(myWebView);
        }*/

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
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    Log.d("shouldOverrideUrl", url);
                    if(url.contains("ali_trackid=2:mm_") || url.contains("ali_trackid=2%3Amm_")){
                        showLeftTishi("已进入返钱模式");
                        webviewBottom.setVisibility(View.VISIBLE);
                        return false;
                    }else if(url.contains("frm=etao")){
                        showLeftTishi("已进入返集分宝模式");
                        webviewBottom.setVisibility(View.VISIBLE);
                        return false;
                    }else{
                        hideLeftTishi();
                    }
                    //http://h5.m.taobao.com/awp/base/order.htm?itemId=535027728789&item_num_id=535027728789&_input_charset=utf-8&buyNow=true&v=0&quantity=1&skuId=3193002206132&exParams=%7B%22id%22%3A%22535027728789%22%2C%22fqbb%22%3A%221%22%7D
                    //https://buy.m.tmall.com/order/confirmOrderWap.htm?enc=%E2%84%A2&buyNow=true&_input_charset=utf-8&itemId=533800099546&skuId=3184727994633&quantity=1&divisionCode=310100&x-itemid=533800099546&x-uid=589338408
                    //进入淘宝下单页面需要判断阿里妈妈是否在线
                    if(url.replace("http://","").replace("https://","").startsWith("h5.m.taobao.com/awp/base/order.htm") ||
                       isTmallOrderPage(url) ){
                        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
                           @Override
                           public void hasLoginCallback() {
                               // TODO: 2016/10/14
                               view.loadUrl(url);
                           }

                           @Override
                           public void nologinCallback() {
                               myWebView.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       myWebView.stopLoading();
                                   }
                               });
                               showFanliLoginDialog();
                           }

                           @Override
                           public void judgeErrorCallback() {
                               // TODO: 2016/10/14     
                           }
                       },VOLLEY_TAG_NAME);
                        return true;
                    }

                    if(isTaobaoItemDetail(url)){//是商品详情页
                        relatedGoodUrl = url.replace("&fqbb=1","");
                        webviewBottom.setVisibility(View.VISIBLE);

                        if(!url.contains("&fqbb=1")){//是新的商品详情页
                            MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.BROWSE_TAOBAO_GOODS_NUM);
                            String itemIdStr = getParamsMapByUrlStr(url).get("id");
                            Long newItemId = 0l;
                            if(itemIdStr!=null){
                                newItemId = Long.parseLong(itemIdStr.trim());
                            }

                            if(currentItemId!=newItemId.longValue() && newItemId!=0l){
                                Log.d("taobao webview detail", "not same item id:"+currentItemId+","+newItemId);
                                currentItemId = newItemId;
                                currentItemUrl = url;
                                getItemFanliInfo(currentItemId);
                                return true;
                            }else{
                                Log.i("taobao webview detail", "same item id:"+newItemId);
                                return false;
                            }
                        }
                    } else if((url.startsWith("https://login.m.taobao.com/login.htm") || url.startsWith("https://login.tmall.com"))
                            && !url.contains("http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm")
                            && !url.contains("https://login.m.taobao.com/login.htm?redirectURL=http://login.taobao.com/member/taobaoke/login.htm")){
                            showLoginPage();
                            return true;
                    }else{
                        cleanCurrentItemId();
                        webviewBottom.setVisibility(View.GONE);
                    }
                    return false;
                }else{
                    return true;
                }
            }
            //兼容 android 5.0 以上
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView webView, WebResourceRequest webResourceRequest) {
                String url = webResourceRequest.getUrl().toString();
                if((url.startsWith("https://login.m.taobao.com/login.htm") || url.startsWith("http://login.m.taobao.com/login.htm"))
                    && url.contains("iframe&")){
                    //needShowLoginDialogForTaobao = true;
                    myWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            webView.stopLoading();
                        }
                    });
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showFanliLoginDialog();
                            } catch (Exception e) {}
                        }
                    });
                }
                if(url.replace("http://","").replace("https://","").startsWith("mclient.alipay.com/h5/cashierPay.htm") ||
                        url.replace("http://","").replace("https://","").startsWith("buyertrade.taobao.com/trade/pay_success.htm") ||
                        url.replace("http://","").replace("https://","").startsWith("buy.tmall.com/order/paySuccess.htm")) {

                    MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SUCCESS_BUY_FOR_TAOBAO);
                }
                return null;
            }

            //兼容 android 4.4 及以上
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {//也能拦截iframe的链接,图片,css,js等
                //淘宝立即购买的登录
                //http://login.m.taobao.com/login.htm?ttid=h5%40iframe&tpl_redirect_url=http%3A%2F%2Fh5.m.taobao.com%2Fother%2Floginend.html%3Forigin%3Dhttp%253A%252F%252Fh5.m.taobao.com
                if((url.startsWith("https://login.m.taobao.com/login.htm") || url.startsWith("http://login.m.taobao.com/login.htm"))
                    && url.contains("iframe&")){
                    //needShowLoginDialogForTaobao = true;
                    myWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            view.stopLoading();
                        }
                    });
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                showFanliLoginDialog();
                            } catch (Exception e) {}
                        }
                    });
                }
                if(url.replace("http://","").replace("https://","").startsWith("mclient.alipay.com/h5/cashierPay.htm") ||
                        url.replace("http://","").replace("https://","").startsWith("buyertrade.taobao.com/trade/pay_success.htm") ||
                        url.replace("http://","").replace("https://","").startsWith("buy.tmall.com/order/paySuccess.htm")) {

                    MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SUCCESS_BUY_FOR_TAOBAO);
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                /*if(isTaobaoItemDetail(url)){
                    doGoBack = false;
                    url = url.replace("&fqbb=1", "");
                    getItemFanliInfo(currentItemId);
                    return;
                }*/

                super.onPageFinished(view, url);

                preWebViewUrl = url;//为了解决用户没登录就直接点击立即购买，弹出登录框我们拦截到然后到自己的登录页，登录后需要跳转回来的

                /*if((url.startsWith("http://h5.m.taobao.com/cart/order.html") || url.startsWith("https://h5.m.taobao.com/cart/order.html"))
                        && url.contains("#bridgeDetail-address_1")){
                    String s = "http://buy.m.tmall.com/order/addressList.htm?enableStation=true&requestStationUrl=%2F%2Fstationpicker-i56.m.taobao.com%2Finland%2FshowStationInPhone.htm&_input_charset=utf8&hidetoolbar=true&bridgeMessage=true&buyUrl=";
                    try {
                        String reloadUrl = s + URLEncoder.encode(url, "UTF-8");
                        myWebView.loadUrl(reloadUrl);
                        return;
                    } catch (Exception e) {

                    }
                }*/

                if(url.equals("https://login.m.taobao.com/login.htm?redirectURL=http://login.taobao.com/member/taobaoke/login.htm?is_login=1&loginFrom=wap_alimama")){
                    webviewTitleText.setText("淘宝账号登录");
                }else{
                    webviewTitleText.setText("");
                }

                Log.i("taobao webview url", url);

                if(doGoBack){
                    if(isTaobaoItemDetail(url)){//是商品详情页
                        webviewBottom.setVisibility(View.VISIBLE);

                        //获取商品返利信息
                        String itemIdStr = getParamsMapByUrlStr(url).get("id");
                        Long newItemId = 0l;
                        if(itemIdStr!=null){
                            newItemId = Long.parseLong(itemIdStr.trim());
                        }

                        if(currentItemId!=newItemId.longValue() && newItemId!=0l){
                            Log.d("taobao webview detail", "not same item id:"+currentItemId+","+newItemId);
                            currentItemId = newItemId;
                            currentItemUrl = url.replace("&fqbb=1", "");
                            getItemFanliInfo(currentItemId);
                        }else{
                            Log.i("taobao webview detail", "same item id:"+newItemId);
                        }
                    }else{
                        webviewBottom.setVisibility(View.GONE);
                    }

                    doGoBack = false;
                }

                if(url.startsWith("http://www.alimama.com/index.htm") || url.startsWith("http://www.alimama.com/index.htm")
                        || url.startsWith("http://media.alimama.com/account/overview.htm")
                        || url.startsWith("https://www.alimama.com/index.htm") || url.startsWith("https://www.alimama.com/index.htm")
                        || url.startsWith("https://media.alimama.com/account/overview.htm")){//已登录
                    if(loginPreUrl!=null){
                        TaobaoInterPresenter.saveLoginCookie(url);

                        if(isTaobaoItemDetail(loginPreUrl)){//是商品详情页
                            String itemIdStr = getParamsMapByUrlStr(loginPreUrl).get("id");
                            Long newItemId = 0l;
                            if(itemIdStr!=null){
                                newItemId = Long.parseLong(itemIdStr.trim());
                                currentItemId = newItemId;
                                currentItemUrl = loginPreUrl.replace("&fqbb=1", "");
                            }

                            getItemFanliInfo(currentItemId);
                        }else{
                            if(myWebView != null){
                                myWebView.loadUrl(loginPreUrl);
                            }
                        }

                        loginPreUrl = null;
                        loginAlimama.setVisibility(View.GONE);
                    }
                }
                String js = "var rmadjs = document.createElement(\"script\");";
                js += "rmadjs.src=\"//www.fanqianbb.com/static/mobile/rmad.js\";";
                js += "document.body.appendChild(rmadjs);";
                view.loadUrl("javascript:" + js);
            }
        });

        //登录阿里妈妈
        loginAlimama = (Button)this.findViewById(R.id.loginAlimama);
        loginAlimama.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(myWebView!=null){
                  loginPreUrl = myWebView.getUrl();
                  showLoginPage();
                }
            }
        });

        //开通返现权限
        ktfxQx = (Button)this.findViewById(R.id.ktfxQx);
        ktfxQx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //http://media.alimama.com/user/limit_status.htm?from=union
                if(myWebView!=null){
                  ktqxPreUrl = myWebView.getUrl();
                  String ktfxQxUrl = "http://pub.alimama.com/myunion.htm";
                  myWebView.loadUrl(ktfxQxUrl);
                  webviewBottom.setVisibility(View.GONE);
                }
            }
        });

        //完善信息（修复权限）
        wsxx = (Button)this.findViewById(R.id.wsxx);
        wsxx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getChannelAdzoneInfo();
            }
        });

        //启动返现
        qdfx = (Button)this.findViewById(R.id.qdfx);
        qdfx.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                getCpsLink();
            }
        });

        //返集分宝
        qdfjfb = (Button)this.findViewById(R.id.qdfjfb);
        qdfjfb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(etaoJfbUrl!=null && !"".equals(etaoJfbUrl.trim())){
                    qdfjfb.setVisibility(View.GONE);
                    if(myWebView!=null){
                      myWebView.loadUrl(etaoJfbUrl);
                    }
                }
            }
        });

        //刷新进入返利
        reflashFl = (Button)this.findViewById(R.id.reflashFl);
        reflashFl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(myWebView != null){
                  String url = myWebView.getUrl();
                  myWebView.loadUrl(url);
                }
            }
        });

        itemfxinfo1 = (TextView) this.findViewById(R.id.itemfxinfo1);
        itemfxinfo2 = (TextView) this.findViewById(R.id.itemfxinfo2);
        itemfxinfo3 = (TextView) this.findViewById(R.id.itemfxinfo3);

        leftDetailInfo = (TextView) this.findViewById(R.id.leftDetailInfo);

        Intent in = getIntent();
        String loadUrl = in.getStringExtra("loadUrl");
        originLoadUrl = loadUrl;
        if(loadUrl.startsWith("//")){
            loadUrl = "http:"+loadUrl;
        }
        isFromGoodSearch = in.getBooleanExtra("forSearchGoodInfo", false);
        if(isFromGoodSearch){
            webviewToHome.setText("返回搜索列表");
        }else{
            webviewToHome.setText("返回宝宝主页");
        }

        bizString = in.getStringExtra("bizString");
        if(TextUtils.isEmpty(bizString)){
            bizString = "";
        }

        if(loadUrl.equals(myTaobaoPageUrl)){
            initTaobaoItemId = -1l;
            isLoginForMyTaobao();
        }else{
            if(isTaobaoItemDetail(loadUrl)){//是商品详情页
                String itemIdStr = getParamsMapByUrlStr(loadUrl).get("id");
                try{
                    if(itemIdStr!=null && !"".equals(itemIdStr.trim()))
                        initTaobaoItemId = Long.parseLong(itemIdStr);
                }catch(Exception e){}
            }else{
                initTaobaoItemId = -2l;//其它进入WEBVIEW H5页面的入口（如首页轮播图的链接点击）
            }
            if(myWebView != null){
               myWebView.loadUrl(loadUrl);
            }
        }
    }

    @Override
    protected void onResume() {
        if("tbGoodDetail".equals(bizString)){
             MobclickAgent.onPageStart("tbGoodDetail_wv");
        }
        else if("myTaoBao".equals(bizString)){
             MobclickAgent.onPageStart("myTaoBao_wv");
        }
        else if("taobao".equals(bizString)){
             MobclickAgent.onPageStart("taobao_wv");
        }
        else if("tmall".equals(bizString)){
             MobclickAgent.onPageStart("tmall_wv");
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        if("tbGoodDetail".equals(bizString)){
            MobclickAgent.onPageEnd("tbGoodDetail_wv");
        }
        else if("myTaoBao".equals(bizString)){
            MobclickAgent.onPageEnd("myTaoBao_wv");
        }
        else if("taobao".equals(bizString)){
            MobclickAgent.onPageEnd("taobao_wv");
        }
        else if("tmall".equals(bizString)){
            MobclickAgent.onPageEnd("tmall_wv");
        }
        super.onPause();
    }

    private void showFanliLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
        builder.setMessage("登录到返利模式才会有返利.");

        builder.setCancelable(false);
        builder.setPositiveButton("返利登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loginPreUrl = preWebViewUrl;
                showLoginPage();
            }
        });
        builder.create().show();
    }

    private void showHasPayDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebviewActivity.this);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("购买成功");

        builder.setMessage("付款后就可以查返利订单啦，快去检查是否返利成功");

        builder.setCancelable(false);
        builder.setPositiveButton("查订单", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payCloseWebviewActivity(2);
            }
        });
        builder.setNegativeButton("回首页", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                payCloseWebviewActivity(1);
            }
        });
        builder.create().show();
    }

    private boolean isLoginUrl(String url){
        String replaceUrl = url.replace("https://", "").replace("http://", "");

        if(replaceUrl.startsWith("login.taobao.com")
                || replaceUrl.startsWith("login.m.taobao.com")){
            return true;
        }

        return false;
    }

    private boolean isTaobaoItemDetail(String url){
        String replaceUrl = url.replace("https://", "").replace("http://", "");

        if(replaceUrl.startsWith("h5.m.taobao.com/awp/core/detail.htm")
                || replaceUrl.startsWith("detail.m.tmall.com")
                || replaceUrl.startsWith("www.taobao.com/market/ju/detail_wap.php")
                || replaceUrl.startsWith("item.taobao.com/item.htm")
                || replaceUrl.startsWith("detail.tmall.com/item.htm")
                ){
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    public void goBack(){
        if(myWebView == null) {
          return;
        }
        cleanCurrentItemId();
        String currentWebviewUrl = myWebView.getUrl();
        String contentWebviewUrl = "";
        if(currentWebviewUrl!=null && !"".equals(currentWebviewUrl.trim())){
            contentWebviewUrl = currentWebviewUrl.replace("http://","").replace("https://","");
            if(initTaobaoItemId==-1){//我的淘宝进入当前activity
                if(!currentWebviewUrl.contains("login.taobao.com/member/login.jhtml")
                        && !currentWebviewUrl.contains("h5.m.taobao.com/mlapp/mytaobao.html")
                        && !currentWebviewUrl.contains("login.m.taobao.com/login.htm")){
                    myWebView.goBack();
                    doGoBack = true;
                    return;
                }
            }else if(contentWebviewUrl.startsWith("login.m.taobao.com/login.htm")){
                if(myWebView == null){
                    return;
                }
                doGoBack = true;
                myWebView.loadUrl(relatedGoodUrl);
                return;
            } else if(initTaobaoItemId>0){//商品详情进入当前activity
                String itemIdStr = getParamsMapByUrlStr(currentWebviewUrl).get("id");
                try{
                    if(itemIdStr!=null && !"".equals(itemIdStr.trim())){
                        Long itemid = Long.parseLong(itemIdStr);
                        if(initTaobaoItemId.longValue() != itemid.longValue()){
                            myWebView.goBack();
                            doGoBack = true;
                            return;
                        }
                    }else{
                        myWebView.goBack();
                        doGoBack = true;
                        return;
                    }
                }catch(Exception e){
                    myWebView.goBack();
                    doGoBack = true;
                    return;
                }
            }else{
                if(myWebView.canGoBack()){
                    myWebView.goBack();
                    doGoBack = true;
                    return;
                }
            }
        }else{
            Log.i("in goback url--->","null or empty");
        }
        closeWebviewActivity();
    }

    protected void onDestroy() {
        TaobaoInterPresenter.cancelTagedRuquests(VOLLEY_TAG_NAME);
        if(myWebView!=null){
            ViewGroup viewGroup = (ViewGroup) myWebView.getParent();
            if(viewGroup!=null){
                viewGroup.removeView(myWebView);
            }
            myWebView.removeAllViews();
            myWebView.destroy();
            myWebView=null;
        }

        back.setOnClickListener(null);
        webviewToHomeLl.setOnClickListener(null);
        loginAlimama.setOnClickListener(null);
        ktfxQx.setOnClickListener(null);
        qdfx.setOnClickListener(null);
        qdfjfb.setOnClickListener(null);
        reflashFl.setOnClickListener(null);
        super.onDestroy();
    }

    private void payCloseWebviewActivity(int type){
        if(myWebView == null){
            return;
        }
        currentItemId = 0l;
        currentItemIsGaofan = 0;

        clearWebview();
        myWebView.setVisibility(View.GONE);

        Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        if(type==1){//回首页
            intent.putExtra("toFirstPage", true);
        }else if(type==2){//查订单
            intent.putExtra("toOrderPage", true);
        }

        startActivity(intent);
        finish();
    }

    private void closeWebviewActivity(){
        if(myWebView == null){
            return;
        }
        if(isFromGoodSearch){
            currentItemId = 0l;
            currentItemIsGaofan = 0;

            clearWebview();
            myWebView.setVisibility(View.GONE);

            isFromGoodSearch = false;
            Intent intent = new Intent(WebviewActivity.this, SearchActivity.class);
            intent.putExtra("intentFromMain",false);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }/*
        if(ktqxPreUrl!=null && !"".equals(ktqxPreUrl.trim())){
            myWebView.loadUrl(ktqxPreUrl);
            ktqxPreUrl = null;
        }*/else{
            currentItemId = 0l;
            currentItemIsGaofan = 0;

            clearWebview();
            myWebView.setVisibility(View.GONE);

            Intent intent = new Intent(WebviewActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }

    public void clearWebview(){
        Log.i("clear webview","in clear webview ==================");
        if(myWebView!=null){
           myWebView.loadUrl("file:///android_asset/index.html");
        }
        showFlowBtn(null);
        webviewBottom.setVisibility(View.GONE);
    }

    public void showFanliInfo(Float fxRate, Float fxFee){
        itemfxinfo1.setText("返");
        itemfxinfo2.setText(fxRate+"%");
        itemfxinfo3.setText("     约返"+new DecimalFormat("#.##").format(fxFee)+"元");
        itemfxinfo1.setVisibility(View.VISIBLE);
        itemfxinfo2.setVisibility(View.VISIBLE);
        itemfxinfo3.setVisibility(View.VISIBLE);
        tipArea.setVisibility(View.VISIBLE);
    }

    public void showFanJfbInfo(Long jfbAmount){
        itemfxinfo1.setText("返");
        itemfxinfo2.setText(jfbAmount+"");
        itemfxinfo3.setText("集分宝");
        itemfxinfo1.setVisibility(View.VISIBLE);
        itemfxinfo2.setVisibility(View.VISIBLE);
        itemfxinfo3.setVisibility(View.VISIBLE);
    }

    public void showLeftTishi(String tsxx){
        leftDetailInfo.setText(tsxx);
        leftDetailInfo.setVisibility(View.VISIBLE);
    }

    public void hideLeftTishi(){
        leftDetailInfo.setVisibility(View.GONE);
    }

    public void showRightTishi(String tsxx){
        itemfxinfo1.setText(tsxx);
        itemfxinfo1.setVisibility(View.VISIBLE);

        itemfxinfo2.setVisibility(View.GONE);
        itemfxinfo3.setVisibility(View.GONE);
        tipArea.setVisibility(View.GONE);
    }

    public void showFlowBtn(String jsonInfoFromAlimama){//显示登录按钮/完善信息按钮/开通权限按钮/启动返钱按钮
        Log.d("showFlowBtn", "in showFlowBtn:"+jsonInfoFromAlimama);

        loginAlimama.setVisibility(View.GONE);
        ktfxQx.setVisibility(View.GONE);
        wsxx.setVisibility(View.GONE);
        qdfx.setVisibility(View.GONE);
        qdfjfb.setVisibility(View.GONE);
        leftDetailInfo.setVisibility(View.GONE);
        reflashFl.setVisibility(View.GONE);

        JSONObject jsonObject;
        if(jsonInfoFromAlimama!=null && !"".equals(jsonInfoFromAlimama)){
            jsonObject = JSONObject.parseObject(jsonInfoFromAlimama);
            if(jsonObject!=null){
                int tag = jsonObject.getIntValue("tag");

                if(tag==0){//显示登录按钮
                    loginAlimama.setVisibility(View.VISIBLE);
                }
                if(tag==1){//完善信息按钮（修复权限）；新增媒体、渠道、推广位失败，取CPS链接出现异常
                    wsxx.setVisibility(View.VISIBLE);
                }
                if(tag==2){//开通权限按钮，没有实名认证
                    ktfxQx.setVisibility(View.VISIBLE);
                    //jfbDisplay();
                }
                if(tag==3){//启动返钱模式按钮
                    qdfx.setVisibility(View.VISIBLE);
                }
                if(tag==4){//启动返集分宝模式按钮
                    qdfjfb.setVisibility(View.VISIBLE);
                }
                if(tag==5){//刷新进入返利按钮
                    reflashFl.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void getItemFanliInfo(Long itemId){
        currentItemId = itemId;
        String u = "http%3A%2F%2Fitem.taobao.com%2Fitem.htm%3Fid%3D"+itemId;
        final String itemFanliUrl = "http://pub.alimama.com/items/search.json?toPage=1&perPagesize=40&_input_charset=utf-8&t="+new Date().getTime()+"&q="+u+"&_tb_token_=";

        Log.d("getFanliInfoCallbackUrl", itemFanliUrl);

        //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
        RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, itemFanliUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null && !"".equals(response.trim())){
                            JSONObject jsonObj =null;
                            try{
                                jsonObj = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                reflashFanliDisplay();
                                return;
                            }
                            JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                            if(dataJsonObj!=null){
                                JSONArray pageListJson = dataJsonObj.getJSONArray("pageList");
                                if(pageListJson!=null && pageListJson.size()>0){
                                    JSONObject itemFanliInfoJson = pageListJson.getJSONObject(0);
                                    final Float gfRate = itemFanliInfoJson.getFloat("eventRate");//高返比例
                                    Float gfFeeCount = null;

                                    final Float fxRate = itemFanliInfoJson.getFloat("tkRate");//返现比率
                                    final Float fxFee = itemFanliInfoJson.getFloat("tkCommFee");//返现金额

                                    if(gfRate!=null && gfRate>0){
                                        Float itemPrice = itemFanliInfoJson.getFloat("zkPrice");//折扣价格
                                        if(itemPrice==null){
                                            itemPrice = itemFanliInfoJson.getFloat("reservePrice");//原价
                                        }
                                        gfFeeCount = itemPrice*gfRate/100;
                                    }
                                    final Float gfFee = gfFeeCount;

                                    URI uri = URI.create(itemFanliUrl);
                                    PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
                                    Log.d("cookie itemFanliUrl", s.get(uri).toString());

                                    if((gfFee!=null && gfFee>0) || (fxFee!=null && fxFee>0)){
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    if(gfFee!=null && gfFee>0){
                                                        showFanliInfo(gfRate, gfFee);
                                                        currentItemIsGaofan = 1;
                                                        isLoginForItemdDetail(false);
                                                    }else if(fxFee!=null && fxFee>0){
                                                        showFanliInfo(fxRate, fxFee);
                                                        currentItemIsGaofan = 0;
                                                        isLoginForItemdDetail(false);
                                                    }else{
                                                        jfbDisplay();
                                                        isLoginForItemdDetail(true);
                                                    }
                                                } catch (Exception e) {
                                                    Log.d("getFlInfoCallbackError",e.getMessage());
                                                    showCurrentItemUrl();
                                                }
                                            }
                                        });
                                    }else{
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    jfbDisplay();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }else{//未获取到返利信息
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                jfbDisplay();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 出错了怎么办，显示通信失败信息
                        showCurrentItemUrl();
                    }
                });

        // 把这个请求加入请求队列
        stringRequest.setTag(VOLLEY_TAG_NAME);
        volleyRq.add(stringRequest);
    }

    public void isLoginForItemdDetail(boolean isJfb){
        final boolean isJfbTag = isJfb;
        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
            @Override
            public void hasLoginCallback() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    if(myWebView!=null){
                        try {
                            if(!isJfbTag){
                                isValidCpsUser();//判断用户是否实名认证
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showCurrentItemUrl();
                        }
                    }
                    }
                });
            }

            @Override
            public void nologinCallback() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    if(myWebView!=null){
                        try {
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("tag", 0);
                            showFlowBtn(jsonObj.toJSONString());//显示登录(我要返钱)按钮
                            showCurrentItemUrl();
                        } catch (Exception e) {
                            e.printStackTrace();
                            showCurrentItemUrl();
                        }
                    }
                    }
                });
            }

            @Override
            public void judgeErrorCallback() {

            }
        },VOLLEY_TAG_NAME);
    }


    //判断是否是天猫下单页面
    private boolean isTmallOrderPage(String url){
        if(TextUtils.isEmpty(url)) return false;
        else if(url.replace("http://","").replace("https://","").startsWith("buy.m.tmall.com/order/confirmOrderWap.htm")) {
            return true;
        }
        else if(url.replace("http://","").replace("https://","").startsWith("login.tmall.com/")){
            Map<String, String> paramsMapByUrlStr = getParamsMapByUrlStr(url);
            if(!paramsMapByUrlStr.isEmpty()){
                if((paramsMapByUrlStr.containsKey("redirectURL") && paramsMapByUrlStr.get("redirectURL").contains("detail.m.tmall.com%2Fitem.htm"))){
                    return true;
                }
            }
        }
        return false;
    }

    public void isLoginForMyTaobao(){
        loginPreUrl = myTaobaoPageUrl;
        cleanCurrentItemId();

        TaobaoInterPresenter.judgeAlimamaLogin(new TaobaoInterPresenter.LoginCallback() {
            @Override
            public void hasLoginCallback() {
                if(myWebView!=null){
                    myWebView.loadUrl(myTaobaoPageUrl);
                }
            }

            @Override
            public void nologinCallback() {
                if(myWebView!=null){
                    myWebView.loadUrl(TaobaoInterPresenter.TAOBAOKE_LOGINURL);
                }
            }

            @Override
            public void judgeErrorCallback() {

            }
        },VOLLEY_TAG_NAME);
    }

    public void showLoginPage(){
        String loginPageUrl = "http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion";
        if(myWebView!=null){
           myWebView.loadUrl(loginPageUrl);
        }
        webviewBottom.setVisibility(View.GONE);
        //cleanCurrentItemId();
    }

    public void isValidCpsUser(){
        Log.d("isValidCpsUser", "isValidCpsUser");
        long siteId = TaobaoInterPresenter.loginUserInfoCache.getLong("siteId", 0l);
        long adzoneId = TaobaoInterPresenter.loginUserInfoCache.getLong("adzoneId", 0l);
        long channelId = TaobaoInterPresenter.loginUserInfoCache.getLong("channelId", 0l);

        if(siteId!=0 && adzoneId!=0 && channelId!=0){
            getChannelAdzoneInfo();
        }else{
            long t = new Date().getTime();
            String isValidCpsUserUrl = "http://pub.alimama.com/common/site/generalize/guideList.json?t="+t+"&_input_charset=utf-8";

            URI uri = URI.create(isValidCpsUserUrl);
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            Log.d("cookie isValidCpsUser:",isValidCpsUserUrl+s.get(uri).toString());

            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, isValidCpsUserUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonObj =null;
                                jsonObj = JSONObject.parseObject(response.trim());
                                if(jsonObj!=null){
                                    boolean hasGuide = true;
                                    if(hasGuide){//获取用户的媒体、渠道等信息
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    getChannelAdzoneInfo();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    showCurrentItemUrl();
                                                }
                                            }
                                        });
                                    }
					                /*if(needAddGuid){

					                }*/
                                }else{
                                    Log.d("isValidCpsUserCallback", "需要实名认证");
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject jsonObj = new JSONObject();
                                                jsonObj.put("tag", 2);
                                                //jsonObj.put("url", "http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion");
                                                showFlowBtn(jsonObj.toJSONString());//显示没有权限按钮
                                                showCurrentItemUrl();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                showCurrentItemUrl();
                                            }
                                        }
                                    });
                                    return;
                                }
                            }catch(Exception e){
                                Log.d("isValidCpsUserCallback", "需要实名认证");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            JSONObject jsonObj = new JSONObject();
                                            jsonObj.put("tag", 2);
                                            //jsonObj.put("url", "http://login.taobao.com/member/login.jhtml?style=common&from=alimama&redirectURL=http%3A%2F%2Flogin.taobao.com%2Fmember%2Ftaobaoke%2Flogin.htm%3Fis_login%3d1&full_redirect=true&disableQuickLogin=true&qq-pf-to=pcqq.discussion");
                                            showFlowBtn(jsonObj.toJSONString());//显示没有权限按钮
                                            showCurrentItemUrl();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            showCurrentItemUrl();
                                        }
                                    }
                                });
                                return;
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 出错了怎么办，显示通信失败信息
                            showCurrentItemUrl();
                        }
                    });

            //stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }
    }

    public void getChannelAdzoneInfo(){
        Log.d("getChannelAdzoneInfo", "getChannelAdzoneInfo");
		/*long siteId = TaobaoInterPresenter.loginUserInfoCache.getLong("siteId", 0l);
        long adzoneId = TaobaoInterPresenter.loginUserInfoCache.getLong("adzoneId", 0l);
        long channelId = TaobaoInterPresenter.loginUserInfoCache.getLong("channelId", 0l);

        if(siteId!=0 && adzoneId!=0 && channelId!=0){
            getCpsLink();
        }else{*/
        long t = new Date().getTime();
        String getChannelAdzoneInfoUrl = "http://pub.alimama.com/common/adzone/newSelfAdzone2.json?tag=29&t="+t;

        URI uri = URI.create(getChannelAdzoneInfoUrl);
        PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
        Log.d("getChannelAdzoneInfo:",getChannelAdzoneInfoUrl+s.get(uri).toString());

        //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
        RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getChannelAdzoneInfoUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Long siteId = null;
                        Long adzoneId = null;
                        Long channelId = null;

                        JSONObject jsonObj =null;
                        try{
                            jsonObj = JSONObject.parseObject(response.trim());
                        }catch(Exception e){
                            reflashFanliDisplay();
                            return;
                        }
                        JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                        if(dataJsonObj!=null){
                            JSONArray otherListsArr = dataJsonObj.getJSONArray("otherList");
                            if(otherListsArr!=null && otherListsArr.size()>0){
                                for(int i=0; i<otherListsArr.size(); i++){
                                    JSONObject oneSiteJson = otherListsArr.getJSONObject(i);
                                    if("代购".equals(oneSiteJson.get("name"))){
                                        siteId = oneSiteJson.getLong("siteid");
                                        break;
                                    }
                                }
                            }

                            JSONArray channelslistArr = dataJsonObj.getJSONArray("channelslist");
                            if(channelslistArr!=null && channelslistArr.size()>0){
                                for(int i=0; i<channelslistArr.size(); i++){
                                    JSONObject oneChannelJson = channelslistArr.getJSONObject(i);
                                    if("代购".equals(oneChannelJson.getString("channelName"))){
                                        channelId = oneChannelJson.getLong("channelId");
                                        break;
                                    }
                                }
                            }

                            JSONArray otherAdzonesArr = dataJsonObj.getJSONArray("otherAdzones");
                            if(otherAdzonesArr!=null && otherAdzonesArr.size()>0){
                                for(int i=0; i<otherAdzonesArr.size(); i++){
                                    JSONObject oneAdzonelJson = otherAdzonesArr.getJSONObject(i);
                                    Long sid = Long.parseLong(oneAdzonelJson.getString("id"));
                                    if(sid != null && siteId != null && sid.longValue() == siteId.longValue()){
                                        JSONArray adzoneSubArr = oneAdzonelJson.getJSONArray("sub");
                                        if(adzoneSubArr!=null && adzoneSubArr.size()>0){
                                            JSONObject adzoneSubJson = adzoneSubArr.getJSONObject(0);
                                            adzoneId = adzoneSubJson.getLong("id");
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        boolean lackUserAlimamaInfo = false;

                        SharedPreferences.Editor prefsWriter = TaobaoInterPresenter.loginUserInfoCache.edit();
                        if(siteId!=null && siteId>0){
                            prefsWriter.putLong("siteId", siteId);

                            if(channelId!=null && channelId>0){
                                prefsWriter.putLong("channelId", channelId);

                                if(adzoneId!=null && adzoneId>0){
                                    prefsWriter.putLong("adzoneId", adzoneId);
                                }else{
                                    lackUserAlimamaInfo = true;
                                }
                            }else{
                                lackUserAlimamaInfo = true;
                            }
                        }else{
                            lackUserAlimamaInfo = true;
                        }

                        prefsWriter.commit();

                        if(lackUserAlimamaInfo){//信息不完整
                            try{
                                //新增媒体
                                if(siteId==null || siteId<=0l){
                                    addSite();
                                    return;
                                }

                                //新增渠道
                                if(channelId==null || channelId<=0l){
                                    addChannel();
                                    return;
                                }

                                //新增推广位
                                if(adzoneId==null || adzoneId<=0l){
                                    addAdZone(siteId, channelId);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            Log.d("getCAInfoCallback", "信息完整");
                            Log.d("getCAInfoCallback", "auto to CPSLink");
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        getCpsLink();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            /*if(AutoFanliPresenter.isAutoFanli()){
                                Log.d("getChannelAdzoneInfoCallback", "auto to CPSLink");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            getCpsLink();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }else{
                                Log.d("getChannelAdzoneInfoCallback", "display 启动返现模式 button");
                                JSONObject jsonFxBtnObj = new JSONObject();
                                jsonFxBtnObj.put("tag", 3);
                                showFlowBtn(jsonFxBtnObj.toJSONString());//显示启动返现按钮
                                showCurrentItemUrl();
                            }*/
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showCurrentItemUrl();
                        // 出错了怎么办，显示通信失败信息
                    }
                });

        // 把这个请求加入请求队列
        stringRequest.setTag(VOLLEY_TAG_NAME);
        volleyRq.add(stringRequest);
        //}
    }

    public void getCpsLink(){
        Log.d("getCpsLink", "getCpsLink");
        long siteId = TaobaoInterPresenter.loginUserInfoCache.getLong("siteId", 0l);
        long adzoneId = TaobaoInterPresenter.loginUserInfoCache.getLong("adzoneId", 0l);
        long channelId = TaobaoInterPresenter.loginUserInfoCache.getLong("channelId", 0l);

        if(siteId!=0 && adzoneId!=0 && channelId!=0){
            long t = new Date().getTime();
            String getCpsLinkUrl = "http://pub.alimama.com/common/code/getAuctionCode.json?auctionid="+currentItemId+"&siteid="+siteId+"&adzoneid="+adzoneId+"&t="+t+"&_input_charset=utf-8";
            if(currentItemIsGaofan==1){
                getCpsLinkUrl = "http://pub.alimama.com/common/code/getAuctionCode.json?auctionid="+currentItemId+"&siteid="+siteId+"&adzoneid="+adzoneId+"&t="+t+"&_input_charset=utf-8&scenes=3&channel=tk_qqhd";
            }

            URI uri = URI.create(getCpsLinkUrl);
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            List<HttpCookie> listCookie = s.get(uri);
            Log.d("cookie getCpsLink",getCpsLinkUrl+listCookie.toString());

            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, getCpsLinkUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj =null;
                            try{
                                jsonObj = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                reflashFanliDisplay();
                                return;
                            }
                            JSONObject dataJsonObj = jsonObj.getJSONObject("data");
                            if(dataJsonObj!=null){
                                //final String cpsUrl = dataJsonObj.getString("shortLinkUrl");
                                final String cpsUrl = dataJsonObj.getString("clickUrl");
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            if(myWebView!=null){
                                            myWebView.loadUrl(cpsUrl);
                                            qdfx.setVisibility(View.GONE);
                                            MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.NUMBER_OF_FANLI_FOR_TAOBAO);
                                            //addItemTextInfo("已进入返利模式，", null);
                                            //testDd();
                                         }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            showCurrentItemUrl();
                            // 出错了怎么办，显示通信失败信息
                        }
                    });

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }else{
            return;
        }
    }

    public void jfbDisplay(){
        showRightTishi("该商品没返利");
        hideLeftTishi();
        showCurrentItemUrl();

        /*String jfbUrl = "http://ok.etao.com/api/purchase_detail.do?src=auction_detail&partner=2006&nid=" + currentItemId;

        //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
        RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, jfbUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response!=null && !"".equals(response.trim())){
                            JSONObject jsonObject =null;
                            try{
                                jsonObject = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                showRightTishi("没获取到该商品返利信息");
                                hideLeftTishi();
                            }
                            if(jsonObject!=null){
                                JSONObject r = jsonObject.getJSONObject("result");
                                if(r!=null){
                                    JSONObject bestPlan = r.getJSONObject("bestPlan");
                                    if(bestPlan!=null){
                                        String jfbAmount = bestPlan.getString("rebateSaving");//集分宝数
                                        String cpsHref = bestPlan.getString("cpsHref");//集分宝CPS跳转URL

                                        if(jfbAmount!=null && !"".equals(jfbAmount.trim())
                                                && cpsHref!=null && !"".equals(cpsHref.trim()) && !isTaobaoItemDetail(cpsHref)){
                                            int jfbInt = Integer.parseInt(jfbAmount.trim());
                                            if(jfbInt>0){
                                                Long jfbAmountLong = 0l;
                                                if(jfbAmount!=null && !"".equals(jfbAmount.trim())){
                                                    jfbAmountLong = Long.parseLong(jfbAmount.trim());
                                                    if(jfbAmountLong!=null && jfbAmountLong>0){
                                                        showFanJfbInfo(jfbAmountLong);

                                                        if(AutoFanliPresenter.isAutoFanli()){
                                                            myWebView.loadUrl(cpsHref);
                                                        }else{
                                                            Log.d("jfbDisplay", "display 启动返集分宝模式 button");
                                                            etaoJfbUrl = cpsHref;
                                                            JSONObject jsonFjfbBtnObj = new JSONObject();
                                                            jsonFjfbBtnObj.put("tag", 4);
                                                            showFlowBtn(jsonFjfbBtnObj.toJSONString());//显示启动返集分宝按钮
                                                        }

                                                        return;
                                                    }
                                                }
                                            }else{
                                                //显示没有返利信息
                                                showRightTishi("该商品没返利");
                                                hideLeftTishi();
                                                showCurrentItemUrl();
                                            }
                                        }else{
                                            //显示没有返利信息
                                            showRightTishi("该商品没返利");
                                            hideLeftTishi();
                                            showCurrentItemUrl();
                                        }
                                    }else{
                                        //显示没有返利信息
                                        showRightTishi("该商品没返利");
                                        hideLeftTishi();
                                        showCurrentItemUrl();
                                    }
                                }
                            }
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 出错了怎么办，显示通信失败信息
                    }
                });

        // 把这个请求加入请求队列
        volleyRq.add(stringRequest);*/
    }

    public void addSite(){
        try{
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            URI uri = URI.create("http://www.alimama.com");
            List<HttpCookie> listAlimamaCookies = s.get(uri);
            String tbTokenCookie = null;
            if(listAlimamaCookies!=null && listAlimamaCookies.size()>0){
                for(HttpCookie c : listAlimamaCookies){
                    if(c.getName().equals("_tb_token_")){
                        tbTokenCookie = c.getValue();
                    }
                }
            }

            //新增媒体
            //"http://pub.alimama.com/common/site/generalize/guideAdd.json?name=" + encodeURIComponent("代购")(媒体名称) + "&categoryId=24&_tb_token_=" + itemInfo.tbToken + "&account1=" + itemInfo.account(用户的旺旺名)
            String mmNick = TaobaoInterPresenter.loginUserInfoCache.getString("mmNick", null);
            String addSiteUrl = "http://pub.alimama.com/common/site/generalize/guideAdd.json?name=" + URLEncoder.encode("代购", "UTF-8") + "&categoryId=24&_tb_token_=" + tbTokenCookie + "&account1=" + URLEncoder.encode(mmNick, "UTF-8");
            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, addSiteUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj =null;
                            try{
                                jsonObj = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                wsxxDisplay();
                                return;
                            }
                            if(jsonObj.getBooleanValue("ok") && jsonObj.getJSONObject("info")!=null && jsonObj.getJSONObject("info").getBooleanValue("ok")){
                                //新增成功，调用新增渠道的方法
                                addChannel();
                            }else{
                                wsxxDisplay();
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            wsxxDisplay();
                        }
                    });

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }catch(Exception e){
            wsxxDisplay();
        }
    }

    public void addChannel(){
        try{
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            URI uri = URI.create("http://www.alimama.com");
            List<HttpCookie> listAlimamaCookies = s.get(uri);
            String tbTokenCookie = null;
            if(listAlimamaCookies!=null && listAlimamaCookies.size()>0){
                for(HttpCookie c : listAlimamaCookies){
                    if(c.getName().equals("_tb_token_")){
                        tbTokenCookie = c.getValue();
                    }
                }
            }

            //新增渠道
            String addChannelUrl = "http://pub.alimama.com/common/channel/channelSave.json?act=new&channelName=" + URLEncoder.encode("代购", "UTF-8") + "&selectAdzoneIds=&_tb_token_=" + tbTokenCookie;
            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, addChannelUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj =null;
                            try{
                                jsonObj = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                wsxxDisplay();
                                return;
                            }
                            if(jsonObj.getBooleanValue("ok") && jsonObj.getJSONObject("info")!=null && jsonObj.getJSONObject("info").getBooleanValue("ok")){
                                getChannelAdzoneInfo();
                            }else{
                                wsxxDisplay();
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            wsxxDisplay();
                        }
                    });

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }catch(Exception e){
            wsxxDisplay();
        }
    }

    public void addAdZone(Long siteId, Long channelId){
        try{
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            URI uri = URI.create("http://www.alimama.com");
            List<HttpCookie> listAlimamaCookies = s.get(uri);
            String tbTokenCookie = null;
            if(listAlimamaCookies!=null && listAlimamaCookies.size()>0){
                for(HttpCookie c : listAlimamaCookies){
                    if(c.getName().equals("_tb_token_")){
                        tbTokenCookie = c.getValue();
                    }
                }
            }

            final Long siteId2 = siteId;
            final Long channelId2 = channelId;

            //新增广告位
            String addAdZoneUrl = "http://pub.alimama.com/common/adzone/selfAdzoneCreate.json?promotion_type=29" + URLEncoder.encode("#", "UTF-8") + "29&gcid=8&siteid=" + siteId + "&selectact=add&newadzonename=" + URLEncoder.encode("代购", "UTF-8") + "&channelIds=" + channelId + "&_tb_token_=" + tbTokenCookie;
            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, addAdZoneUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj =null;
                            try{
                                jsonObj = JSONObject.parseObject(response.trim());
                            }catch(Exception e){
                                wsxxDisplay();
                                return;
                            }
                            if(jsonObj.getBooleanValue("ok") && jsonObj.getJSONObject("info")!=null && jsonObj.getJSONObject("info").getBooleanValue("ok")){
                                //新增成功，则新增鹊桥的广告位
                                addQueqiaoAdZone(siteId2, channelId2);
                            }else{
                                wsxxDisplay();
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            wsxxDisplay();
                        }
                    });

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }catch(Exception e){
            wsxxDisplay();
        }
    }

    public void addQueqiaoAdZone(Long siteId, Long channelId){
        try{
            PersistentCookieStore s = new PersistentCookieStore(MyApplication.getContext());
            URI uri = URI.create("http://www.alimama.com");
            List<HttpCookie> listAlimamaCookies = s.get(uri);
            String tbTokenCookie = null;
            if(listAlimamaCookies!=null && listAlimamaCookies.size()>0){
                for(HttpCookie c : listAlimamaCookies){
                    if(c.getName().equals("_tb_token_")){
                        tbTokenCookie = c.getValue();
                    }
                }
            }

            //新增鹊桥广告位
            String addAdZoneUrl = "http://pub.alimama.com/common/adzone/selfAdzoneCreate.json?promotion_type=59" + URLEncoder.encode("#", "UTF-8") + "59&gcid=8&siteid=" + siteId + "&selectact=add&newadzonename=" + URLEncoder.encode("代购_鹊桥", "UTF-8") + "&channelIds=" + channelId + "&_tb_token_=" + tbTokenCookie;
            //RequestQueue volleyRq = Volley.newRequestQueue(this, new OkHttpStack(this));
            RequestQueue volleyRq = TaobaoInterPresenter.getVolleyRequestQueue();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, addAdZoneUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObj = JSONObject.parseObject(response);
                            if(jsonObj.getBooleanValue("ok") && jsonObj.getJSONObject("info")!=null && jsonObj.getJSONObject("info").getBooleanValue("ok")){
                                Long reflashId = currentItemId;
                                currentItemId = 0l;
                                currentItemIsGaofan = 0;
                                if(myWebView != null) {
                                    myWebView.loadUrl("http://h5.m.taobao.com/awp/core/detail.htm?id=" + reflashId);
                                }
                                }else{
                                wsxxDisplay();
                            }
                        }},
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // 出错了怎么办，显示通信失败信息
                            wsxxDisplay();
                        }
                    });

            // 把这个请求加入请求队列
            stringRequest.setTag(VOLLEY_TAG_NAME);
            volleyRq.add(stringRequest);
        }catch(Exception e){

        }
    }

    private void wsxxDisplay(){
        JSONObject jsonObjShowReflash = new JSONObject();
        jsonObjShowReflash.put("tag", 1);
        showFlowBtn(jsonObjShowReflash.toJSONString());
        showCurrentItemUrl();
    }

    private void reflashFanliDisplay(){
        JSONObject jsonObjShowReflash = new JSONObject();
        jsonObjShowReflash.put("tag", 5);
        showFlowBtn(jsonObjShowReflash.toJSONString());
        showCurrentItemUrl();
    }

    private void showCurrentItemUrl(){
        if(currentItemUrl!=null && !"".equals(currentItemUrl)){
            String url = currentItemUrl;
            int jinIndex = url.indexOf("#");
            if(jinIndex>0){
                url = url.substring(0, jinIndex) + "&fqbb=1" + url.substring(jinIndex);
            }else{
                url = url+"&fqbb=1";
            }
            if(myWebView!=null){
              myWebView.loadUrl(url);
            }
        }
    }

    public void cleanCurrentItemId(){
        this.currentItemId = 0l;
        this.currentItemIsGaofan = 0;
        this.currentItemUrl = null;
    }

    public static Map<String, String> getParamsMapByUrlStr(String url) {
        String queryStr = null;

        if(url==null || "".equals(url.trim())){
            return new HashMap<String, String>();
        }

        int whIndex = url.indexOf("?");
        if(whIndex>=0){
            queryStr = url.substring(whIndex+1);
        }

        return getParamsMapByQueryStr(queryStr);
    }

    public static Map<String, String> getParamsMapByQueryStr(String queryStr) {
        Map<String, String> paramsMap = new HashMap<String, String>();

        if(queryStr==null || "".equals(queryStr.trim())){
            return paramsMap;
        }

        int i = queryStr.indexOf("#");
        if(i>0){
            queryStr = queryStr.substring(0, i);
        }

        if(queryStr!=null && !"".equals(queryStr.trim())){
            String[] paramsArr = queryStr.split("&");
            if(paramsArr!=null && paramsArr.length>0){
                for(String oneParamPair : paramsArr){
                    if(oneParamPair!=null && !"".equals(oneParamPair.trim())){
                        String[] oneParamKv = oneParamPair.split("=");
                        if(oneParamKv!=null && oneParamKv.length==2){
                            paramsMap.put(oneParamKv[0].trim(), oneParamKv[1].trim());
                        }else if(oneParamKv!=null && oneParamKv.length==1){
                            paramsMap.put(oneParamKv[0].trim(), "");
                        }
                    }
                }
            }
        }

        return paramsMap;
    }
}
