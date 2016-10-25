package cn.fuyoushuo.fqbb.view.flagment;

import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.net.URLEncoder;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;

/**
 * Created by MALIANG on 2016/10/20.
 * 用于展现京东的搜索结果页面
 */
public class JdSearchResFlagment extends BaseFragment implements SearchFlagment.doUpdateWithQ{

    public static final String TAG_NAME = "jdSearchResFlagment";

    private String q = "";

    public static String jd_search_url_prifix = "http://so.m.jd.com/ware/search.action?keyword=";

    @Bind(R.id.jd_search_webview_container)
    LinearLayout myJdContainer;

    WebView myJdWebView;

    @Override
    protected int getRootLayoutId() {
        return R.layout.view_jd_search;
    }

    @Override
    protected void initView() {
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
                    if(replaceUrl.startsWith(jd_search_url_prifix)){
                        view.loadUrl(url);
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
            }
        });
        myJdContainer.addView(myJdWebView);
        if(!isInit){
            myJdWebView.loadUrl(getCurrentUrl(this.q));
        }
    }

    @Override
    protected void initData() {

    }
    /**
     * 获取当前url
     * @param q
     * @return
     */
    private String getCurrentUrl(String q){
        if(TextUtils.isEmpty(q)) return "";
        try{
          return jd_search_url_prifix+ URLEncoder.encode(q,"utf-8");
        }catch (Exception e){
          return q;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myJdWebView != null){
           myJdWebView.removeAllViews();
           myJdWebView.destroy();

        }
    }

    @Override
    public void updateQ(String q) {
       if(!isInit){
           this.q = q;
       }else{
           // TODO: 2016/10/24
           this.q = q;
           if(myJdWebView != null){
               myJdWebView.loadUrl(getCurrentUrl(this.q));
           }
       }
    }
}
