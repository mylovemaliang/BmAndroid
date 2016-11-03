package cn.fuyoushuo.fqbb.view.flagment;

import android.os.Bundle;
import android.os.PatternMatcher;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.JdGoodDetailPresenter;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.view.view.JdGoodDetailView;

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


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!TextUtils.isEmpty(getArguments().getString("initUrl",""))){
            this.initUrl = getArguments().getString("initUrl","");
            this.loadUrl = getArguments().getString("initUrl","");
        }
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
        //myJdWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        myJdWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myJdWebView.getSettings().setDomStorageEnabled(true);
        myJdWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myJdWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        myJdWebView.requestFocusFromTouch();
        myJdWebView.setWebChromeClient(new WebChromeClient());

        myJdWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    //http://item.m.jd.com/ware/view.action?wareId=3332179
                    if(replaceUrl.startsWith("item.m.jd.com/ware/view.action")){
                         String itemId = getJdItemId(replaceUrl);


                    }
                    return false;
                } else{
                    return true;
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webViewArea.addView(myJdWebView);
        return inflate;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(myJdWebView != null){
            myJdWebView.loadUrl(initUrl);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //获取商品的id
    private String getJdItemId(String url){
        String result = "";
        if(TextUtils.isEmpty(url)) return result;
        //http://item.m.jd.com/ware/view.action?wareId=3332179
        if(url.startsWith("item.m.jd.com/ware/view.action")){
            Pattern pattern = Pattern.compile("wareId=([0-9]*)");
            Matcher matcher = pattern.matcher(url);
            String groupResult = matcher.group(1);
            if(!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        //http://item.m.jd.com/product/10076124548.html?sid=dbfc3c8a253faf33265f643e30780ebf
        if(url.startsWith("item.m.jd.com/product/")){
            Pattern pattern = Pattern.compile("/product/([0-9]*)\\.html");
            Matcher matcher = pattern.matcher(url);
            String groupResult = matcher.group(1);
            if(!TextUtils.isEmpty(groupResult)) result = groupResult;
        }
        return result;
    }

    //获取当前商品的页面
    private void loadGoodPage(String itemId){
        if(TextUtils.isEmpty(itemId)) return;
        String loadUrl = "http://item.m.jd.com/product/"+itemId+".html";
        localLoginPresent.isFqbbLocalLogin(new LocalLoginPresent.LoginCallBack() {
            @Override
            public void localLoginSuccess() {
                      
            }

            @Override
            public void localLoginFail() {

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

    }

    @Override
    public void onGetJdFanliSucc(JSONObject result) {

    }
}
