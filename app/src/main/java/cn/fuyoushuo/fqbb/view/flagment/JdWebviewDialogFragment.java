package cn.fuyoushuo.fqbb.view.flagment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.components.support.RxDialogFragment;
import com.umeng.analytics.MobclickAgent;

import org.jsoup.helper.DataUtil;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.presenter.impl.JdGoodDetailPresenter;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.view.activity.UserLoginActivity;
import cn.fuyoushuo.fqbb.view.view.JdGoodDetailView;
import rx.functions.Action1;

/**
 * 处理京东详情页的相关逻辑
 * Created by QA on 2016/11/3.
 */
public class JdWebviewDialogFragment extends RxDialogFragment implements JdGoodDetailView {


    private String initUrl = "";

    private String currentLoadGoodUrl = "";

    private String currentItemId = "";

    @Bind(R.id.jd_wv)
    WebView myJdWebView;

    LocalLoginPresent localLoginPresent;

    JdGoodDetailPresenter jdGoodDetailPresenter;

    @Bind(R.id.wv_back_area)
    RelativeLayout backView;

    @Bind(R.id.fq_jd_tip_area)
    FrameLayout fanliTipLayout;

    @Bind(R.id.jd_left_tip_text)
    TextView leftTipText;

    @Bind(R.id.jd_right_tip_text)
    TextView rightTipText;

    @Bind(R.id.jd_hide_tip)
    TextView hideTipText;

    @Bind(R.id.show_fanli)
    FrameLayout showFanliLayout;

    @Bind(R.id.jd_wv_close)
    RelativeLayout closeArea;

    private String fromWhere = "main";

    /**
     * 0 默认情况 1 去登陆 2 重新获取CPS链接  3 重新获取商品CPS跳转
     */
    private int leftTipBiz = 0;

    /**
     * 是否登录
     */
    private boolean isLocalLogin = false;

    /**
     * 是否进入返利
     */
    private boolean isHasFanli = false;


    private boolean isFanliState = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TextUtils.isEmpty(getArguments().getString("initUrl", ""))) {
            this.initUrl = getArguments().getString("initUrl", "");
            this.currentLoadGoodUrl = "";
            this.currentItemId = "";
        }
        fromWhere = getArguments().getString("fromWhere","main");
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
        jdGoodDetailPresenter = new JdGoodDetailPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.flagment_jd_webview, container);
        ButterKnife.bind(this, inflate);
        // TODO: 2016/11/3  初始化VIEW
        myJdWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myJdWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myJdWebView.getSettings().setDomStorageEnabled(true);

        myJdWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myJdWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        myJdWebView.requestFocusFromTouch();
        myJdWebView.setWebChromeClient(new WebChromeClient());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            myJdWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(myJdWebView, true);
        }

        myJdWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                 if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")) {
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    //http://item.m.jd.com/ware/view.action?wareId=3332179
                    if (isPageGoodDetail(url) && url.indexOf("jd_pop") == -1) {
                        if (!url.contains("#ns")) {
                            String itemId = getJdItemId(replaceUrl);
                            loadGoodPage(itemId);
                        }
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (jdGoodDetailPresenter.isUserFanqianMode(url)) {
                    MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.NUMBER_OF_FANLI_FOR_JD);
                    leftTipText.setText("返钱模式");
                    leftTipText.setClickable(false);
                    fanliTipLayout.setVisibility(View.VISIBLE);
                    return false;
                }
                if (isPageGoodDetail(url) && url.indexOf("jd_pop") > -1){
                    isFanliState = true;
                }
                if(isPageGoodDetail(url) && url.contains("#ns")){
                    Log.d("jdGoodDetail",url);
                    return false;
                }
                if(url.replace("http://","").replace("https://","").startsWith("pay.m.jd.com/cpay/finish.html")){
                     MobclickAgent.onEvent(MyApplication.getContext(),EventIdConstants.SUCCESS_BUY_FOR_JD);
                }

                //http://p.m.jd.com/norder/order.action?wareId=1750531&wareNum=1&enterOrder=true&sid=13c72c6665914925103c4af7aff54ebe
                //付款的时候拦截
                if(url.replace("http://","").replace("https://","").startsWith("p.m.jd.com/norder/order.action") && url.indexOf("wareNum=1") > -1 && url.indexOf("enterOrder") > -1){
                     if(!isHasFanli){
                         return false;
                     }else{
                         if(!isLocalLogin){
                             showFanliLoginDialog();
                             return true;
                         }
                     }
                }
                //购物车结算时拦截
                //http://p.m.jd.com/norder/order.action?sid=b13ffb9032fbd3a26dc0471717cb454f&flowType=&page_param=
                else if(url.replace("http://","").replace("https://","").startsWith("p.m.jd.com/norder/order.action") && url.indexOf("wareNum=1") == -1 && url.indexOf("enterOrder") > -1){
                    myJdWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            showFanliTipDialogWhenClickBalance();
                        }
                    });
                }
                return false;
            }

            //兼容 android 5.0 以上
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView webView, WebResourceRequest webResourceRequest) {
                String url = webResourceRequest.getUrl().toString();
                if(isHasFanli && !isFanliState && !TextUtils.isEmpty(url) && url.replace("http://","").replace("https://","").startsWith("p.m.jd.com/cart/add.json")){
                    myJdWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            showFanliTipDialogWhenClickCart();
                        }
                    });
                     String result = "{}";
                     InputStream resultInputStream = null;
                    try {
                        resultInputStream = new ByteArrayInputStream(result.getBytes("utf-8"));
                        WebResourceResponse webResourceResponse = new WebResourceResponse("application/json","utf-8",resultInputStream);
                        return webResourceResponse;
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }finally {
                        if(resultInputStream != null){
                            try {
                                resultInputStream.close();
                            } catch (IOException e) {

                            }
                        }
                    }
                }
                return null;
            }

            //兼容 android 4.4 及以上
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {//也能拦截iframe的链接,图片,css,js等
                if(isHasFanli && !isFanliState && !TextUtils.isEmpty(url) && url.replace("http://","").replace("https://","").startsWith("p.m.jd.com/cart/add.json")){
                    myJdWebView.post(new Runnable() {
                        @Override
                        public void run() {
                            showFanliTipDialogWhenClickCart();
                        }
                    });
                    String result = "{}";
                    InputStream resultInputStream = null;
                    try {
                        resultInputStream = new ByteArrayInputStream(result.getBytes("utf-8"));
                        WebResourceResponse webResourceResponse = new WebResourceResponse("application/json","utf-8",resultInputStream);
                        return webResourceResponse;
                    } catch (UnsupportedEncodingException e) {
                        return null;
                    }finally {
                        if(resultInputStream != null){
                            try {
                                resultInputStream.close();
                            } catch (IOException e) {

                            }
                        }
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (jdGoodDetailPresenter.isUserFanqianMode(url)) {
                    fanliTipLayout.setVisibility(View.VISIBLE);
                } else if (isPageGoodDetail(url)) {
                    fanliTipLayout.setVisibility(View.VISIBLE);
                } else {
                    fanliTipLayout.setVisibility(View.GONE);
                    showFanliLayout.setVisibility(View.GONE);
                }
                String js = "var rmadjs = document.createElement(\"script\");";
                js += "rmadjs.src=\"//www.fanqianbb.com/static/mobile/rmadjd.js\";";
                js += "document.body.appendChild(rmadjs);";
                view.loadUrl("javascript:" + js);
                super.onPageFinished(view, url);
            }
        });
        return inflate;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(backView).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        goback();
                    }
                });

        RxView.clicks(leftTipText).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (leftTipBiz == 0) return;
                        else if (leftTipBiz == 1) {
                            Intent intent = new Intent(getActivity(), UserLoginActivity.class);
                            if("main".equals(fromWhere)){
                                intent.putExtra("biz", "MainToJdWv");
                            }
                            else if("search".equals(fromWhere)){
                                intent.putExtra("biz","SearchToJdWv");
                            }
                            startActivity(intent);
                        }
                        else if (leftTipBiz == 2) {

                        }
                    }
                });

        RxView.clicks(hideTipText).compose(this.<Void>bindToLifecycle()).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if(fanliTipLayout.isShown()){
                             fanliTipLayout.setVisibility(View.GONE);
                             showFanliLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

        RxView.clicks(showFanliLayout).compose(this.<Void>bindToLifecycle()).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         if(!(fanliTipLayout.isShown())){
                             showFanliLayout.setVisibility(View.GONE);
                             fanliTipLayout.setVisibility(View.VISIBLE);
                         }
                    }
                });

        RxView.clicks(closeArea).compose(this.<Void>bindToLifecycle()).throttleFirst(1000,TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        dismissAllowingStateLoss();
                    }
                });


        if (myJdWebView != null) {
            myJdWebView.loadUrl(initUrl);
        }
    }


    @Override
    public void onDestroy() {
        if (myJdWebView != null) {
            ViewGroup viewGroup = (ViewGroup) myJdWebView.getParent();
            if(viewGroup!=null){
                viewGroup.removeView(myJdWebView);
            }
            myJdWebView.removeAllViews();
            myJdWebView.destroy();
        }
        localLoginPresent.onDestroy();
        jdGoodDetailPresenter.onDestroy();
        super.onDestroy();
    }

    //http://mitem.jd.hk/product/3336062.html?sid=6891dd56029cece0e25bbf3a7ea56e69
    //http://mitem.jd.hk/ware/view.action?wareId=2457625&cachekey=3114863cc00f80
    private boolean isPageGoodDetail(String url) {
        if (TextUtils.isEmpty(url)) return false;
        String replaceUrl = url.replace("http://", "").replace("https://", "");
        if (replaceUrl.startsWith("item.m.jd.com/ware/view.action")) {
            return true;
        } else if (replaceUrl.startsWith("item.m.jd.com/product/")) {
            return true;
        } else if (replaceUrl.startsWith("mitem.jd.hk/product/")){
            return true;
        } else if(replaceUrl.startsWith("mitem.jd.hk/ware/view.action")){
            return true;
        }
        return false;
    }

    //获取商品的id
    private String getJdItemId(String url) {
        String result = "";
        if (TextUtils.isEmpty(url)) return result;
        //http://item.m.jd.com/ware/view.action?wareId=3332179
        if (url.startsWith("item.m.jd.com/ware/view.action") || url.startsWith("mitem.jd.hk/ware/view.action")) {
            String groupResult = "";
            Pattern pattern = Pattern.compile("wareId=([0-9]*)");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                groupResult = matcher.group(1);
            }
            if (!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        //http://item.m.jd.com/product/10076124548.html?sid=dbfc3c8a253faf33265f643e30780ebf
        if (url.startsWith("item.m.jd.com/product/") || url.startsWith("mitem.jd.hk/product/")) {
            String groupResult = "";
            Pattern pattern = Pattern.compile("/product/([0-9]*)\\.html");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                groupResult = matcher.group(1);
            }
            if (!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        return result;
    }

    private void showFanliLoginDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("登录到返利模式才会获得返利!");

        builder.setCancelable(false);
        builder.setPositiveButton("返利登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), UserLoginActivity.class);
                if("main".equals(fromWhere)){
                   intent.putExtra("biz", "MainToJdWv");
                }
                else if("search".equals(fromWhere)){
                   intent.putExtra("biz","SearchToJdWv");
                }
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //加入购物车的时候进行提示
    private void showFanliTipDialogWhenClickCart() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("在返利模式下加入购物车的商品才能获得返利,亲!");
        builder.setCancelable(false);
        builder.setPositiveButton("去登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(), UserLoginActivity.class);
                if("main".equals(fromWhere)){
                    intent.putExtra("biz", "MainToJdWv");
                }
                else if("search".equals(fromWhere)){
                    intent.putExtra("biz","SearchToJdWv");
                }
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //点击加入购物车结算进行拦截
    private void showFanliTipDialogWhenClickBalance(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("只有在返利模式下加入购物车的商品才能返还返利,亲!");
        builder.setCancelable(false);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    //获取当前商品的页面
    private void loadGoodPage(final String itemId) {
        if (TextUtils.isEmpty(itemId)) return;
        MobclickAgent.onEvent(MyApplication.getContext(), EventIdConstants.BROWSE_JD_GOODS_NUM);
        final String loadUrl = "http://item.m.jd.com/product/" + itemId + ".html";
        isFanliState = false;
        currentLoadGoodUrl = loadUrl;
        currentItemId = itemId;
        fanliTipLayout.setVisibility(View.VISIBLE);
        localLoginPresent.isFqbbLocalLogin(new LocalLoginPresent.LoginCallBack() {
            @Override
            public void localLoginSuccess() {
                leftTipText.setText("处理中");
                leftTipText.setClickable(false);
                isLocalLogin = true;
                jdGoodDetailPresenter.getJdFanliInfo(itemId, loadUrl);
            }

            @Override
            public void localLoginFail() {
                // TODO: 2016/11/3 提示登录
                isLocalLogin = false;
                leftTipText.setText("去登录");
                leftTipText.setClickable(true);
                leftTipBiz = 1;
                jdGoodDetailPresenter.getJdFanliInfo(itemId,"");
            }
        });
    }

    public void reloadGoodPage() {
        if(TextUtils.isEmpty(currentItemId) || TextUtils.isEmpty(currentLoadGoodUrl)){
            return;
        }else{
            isLocalLogin = true;
            jdGoodDetailPresenter.getJdCpsUrl(currentLoadGoodUrl);
        }
    }

    /**
     * webview 倒退
     */
    private void goback(){
         if(myJdWebView != null && myJdWebView.canGoBack()){
             String url = myJdWebView.getUrl();
             if(!TextUtils.isEmpty(url) && jdGoodDetailPresenter.isUserFanqianMode(url) && "main".equals(fromWhere)){
                  //返回首页
                  myJdWebView.loadUrl("http://m.jd.com");
             }else{
                if("search".equals(fromWhere)){
                   dismissAllowingStateLoss();
                }else{
                  myJdWebView.goBack();
                }
             }
         }else{
             dismissAllowingStateLoss();
         }
    }


    //初始化fragment
    public static JdWebviewDialogFragment newInstance(String initUrl,String fromWhere) {
        Bundle args = new Bundle();
        args.putString("initUrl",initUrl);
        args.putString("fromWhere",fromWhere);
        JdWebviewDialogFragment fragment = new JdWebviewDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("jdHomeH5Page");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("jdHomeH5Page");
    }

    //-----------------------------------------回调VIEW的接口------------------------------------------

    @Override
    public void onGetJdFanliFail() {
        isHasFanli = false;
        leftTipText.setText("");
        rightTipText.setText("当前商品没有返利");
    }

    @Override
    public void onGetJdFanliSucc(JSONObject result,String loadUrl) {
       isHasFanli = true;
       float percent = 0.00f;
       float sumPrice = 0.00f;
       if(result != null && !result.isEmpty()){
           if(result.containsKey("rateWl")){
               percent = result.getFloatValue("rateWl");
           }
           if(result.containsKey("itemPriceWl")){
               sumPrice = result.getFloatValue("itemPriceWl");
           }
       }
       String percentString = String.valueOf(DateUtils.getFormatFloat(percent*100));
       String savePoints = String.valueOf(DateUtils.floatToInt(sumPrice*percent*100));
       rightTipText.setText("返"+percentString+"%|"+"约"+savePoints+"积分");
       if(jdGoodDetailPresenter != null && !TextUtils.isEmpty(loadUrl)){
          jdGoodDetailPresenter.getJdCpsUrl(loadUrl);
       }
    }

    @Override
    public void onGetCpsSucc(String cpsUrl) {
        if(myJdWebView != null){
            myJdWebView.loadUrl(cpsUrl);
        }
    }

    @Override
    public void onGetCpsFail() {
        Toast.makeText(MyApplication.getContext(),"获取CPS链接失败,请重试",Toast.LENGTH_SHORT);
        leftTipText.setText("CPS链接获取");
        leftTipText.setClickable(true);
        leftTipBiz = 2;
    }
}
