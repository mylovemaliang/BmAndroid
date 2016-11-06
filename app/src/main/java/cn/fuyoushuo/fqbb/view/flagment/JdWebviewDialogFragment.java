package cn.fuyoushuo.fqbb.view.flagment;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
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

import org.jsoup.helper.DataUtil;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.commonlib.utils.DateUtils;
import cn.fuyoushuo.fqbb.presenter.impl.JdGoodDetailPresenter;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.view.view.JdGoodDetailView;
import rx.functions.Action1;

/**
 * 处理京东详情页的相关逻辑
 * Created by QA on 2016/11/3.
 */
public class JdWebviewDialogFragment extends DialogFragment implements JdGoodDetailView {


    private String initUrl = "";

    private String loadUrl = "";

    private WebView myJdWebView;

    @Bind(R.id.jd_wv_area)
    RelativeLayout webViewArea;

    LocalLoginPresent localLoginPresent;

    JdGoodDetailPresenter jdGoodDetailPresenter;

    @Bind(R.id.jd_wv_back_area)
    FrameLayout backView;

    @Bind(R.id.fq_jd_tip_area)
    FrameLayout fanliTipLayout;

    @Bind(R.id.jd_left_tip_text)
    TextView leftTipText;

    @Bind(R.id.jd_right_tip_text)
    TextView rightTipText;

    @Bind(R.id.jd_hide_tip)
    TextView hideTipText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!TextUtils.isEmpty(getArguments().getString("initUrl",""))){
            this.initUrl = getArguments().getString("initUrl","");
            this.loadUrl = getArguments().getString("initUrl","");
        }
        setStyle(DialogFragment.STYLE_NORMAL,R.style.fullScreenDialog);
        localLoginPresent = new LocalLoginPresent();
        jdGoodDetailPresenter = new JdGoodDetailPresenter(this);
}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.flagment_jd_webview, container);
        ButterKnife.bind(this,inflate);
        // TODO: 2016/11/3  初始化VIEW
        myJdWebView = new WebView(MyApplication.getContext());
        myJdWebView.getSettings().setJavaScriptEnabled(true);
        //myWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myJdWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myJdWebView.getSettings().setDomStorageEnabled(true);

        myJdWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myJdWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        myJdWebView.requestFocusFromTouch();
        myJdWebView.setWebChromeClient(new WebChromeClient());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            myJdWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CookieManager.getInstance().setAcceptThirdPartyCookies(myJdWebView, true);
        }

        myJdWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    //http://item.m.jd.com/ware/view.action?wareId=3332179
                    if(isPageGoodDetail(url) && url.indexOf("jd_pop") == -1 ){
                        if(!url.contains("#ns")){
                         String itemId = getJdItemId(replaceUrl);
                         loadGoodPage(itemId);
                        }
                    }
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if(jdGoodDetailPresenter.isUserFanqianMode(url)){
                    leftTipText.setText("返钱模式");
                    leftTipText.setClickable(false);
                    fanliTipLayout.setVisibility(View.VISIBLE);
                    return false;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(jdGoodDetailPresenter.isUserFanqianMode(url)){
                    fanliTipLayout.setVisibility(View.VISIBLE);
                }else if(isPageGoodDetail(url)){
                    fanliTipLayout.setVisibility(View.VISIBLE);
                }else{
                    fanliTipLayout.setVisibility(View.GONE);
                }
                super.onPageFinished(view, url);
            }
        });

        webViewArea.addView(myJdWebView);
        return inflate;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(backView).throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                         dismissAllowingStateLoss();
                    }
                });

        if(myJdWebView != null){
            myJdWebView.loadUrl(initUrl);
        }
    }


    @Override
    public void onDestroy() {
        if(myJdWebView != null){
            myJdWebView.removeAllViews();
            myJdWebView.destroy();
        }
        super.onDestroy();
    }




    private boolean isPageGoodDetail(String url){
        if(TextUtils.isEmpty(url)) return false;
        String replaceUrl = url.replace("http://","").replace("https://","");
        if(replaceUrl.startsWith("item.m.jd.com/ware/view.action")){
            return true;
        }
        else if(replaceUrl.startsWith("item.m.jd.com/product/")){
            return true;
        }
            return false;
    }


    //获取商品的id
    private String getJdItemId(String url){
        String result = "";
        if(TextUtils.isEmpty(url)) return result;
        //http://item.m.jd.com/ware/view.action?wareId=3332179
        if(url.startsWith("item.m.jd.com/ware/view.action")){
            String groupResult = "";
            Pattern pattern = Pattern.compile("wareId=([0-9]*)");
            Matcher matcher = pattern.matcher(url);
            if(matcher.find()){
            groupResult = matcher.group(1);
            }
            if(!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        //http://item.m.jd.com/product/10076124548.html?sid=dbfc3c8a253faf33265f643e30780ebf
        if(url.startsWith("item.m.jd.com/product/")){
            String groupResult = "";
            Pattern pattern = Pattern.compile("/product/([0-9]*)\\.html");
            Matcher matcher = pattern.matcher(url);
            if(matcher.find()){
               groupResult = matcher.group(1);
            }
            if(!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        return result;
    }

    //获取当前商品的页面
    private void loadGoodPage(final String itemId){
        if(TextUtils.isEmpty(itemId)) return;
        final String loadUrl = "http://item.m.jd.com/product/"+itemId+".html";
        fanliTipLayout.setVisibility(View.VISIBLE);
        localLoginPresent.isFqbbLocalLogin(new LocalLoginPresent.LoginCallBack() {
            @Override
            public void localLoginSuccess() {
                leftTipText.setText("处理中");
                leftTipText.setClickable(false);
                jdGoodDetailPresenter.getJdFanliInfo(itemId);
                jdGoodDetailPresenter.getJdCpsUrl(loadUrl);

            }

            @Override
            public void localLoginFail() {
                // TODO: 2016/11/3 提示登录
                leftTipText.setText("登录");
                leftTipText.setClickable(true);
                jdGoodDetailPresenter.getJdFanliInfo(itemId);
            }
        });

    }


    //初始化fragment
    public static JdWebviewDialogFragment newInstance(String initUrl) {
        Bundle args = new Bundle();
        args.putString("initUrl",initUrl);
        JdWebviewDialogFragment fragment = new JdWebviewDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //-----------------------------------------回调VIEW的接口------------------------------------------

    @Override
    public void onGetJdFanliFail() {
        rightTipText.setText("返--|约--积分");
    }

    @Override
    public void onGetJdFanliSucc(JSONObject result) {

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
       String percentString = String.valueOf(percent*100);
       String savePoints = String.valueOf(DateUtils.getFormatFloat(sumPrice*percent)*100);
       rightTipText.setText("返"+percentString+"%|"+"约"+savePoints+"积分");
    }

    @Override
    public void onGetCpsSucc(String cpsUrl) {
        if(myJdWebView != null){
            myJdWebView.loadUrl(cpsUrl);
            this.loadUrl = cpsUrl;
        }
    }

    @Override
    public void onGetCpsFail() {
        Toast.makeText(MyApplication.getContext(),"获取CPS链接失败,请重试",Toast.LENGTH_SHORT);
        leftTipText.setText("CPS链接获取");
        leftTipText.setClickable(true);
    }
}