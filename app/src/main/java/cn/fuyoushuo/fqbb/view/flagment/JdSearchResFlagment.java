package cn.fuyoushuo.fqbb.view.flagment;

import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import java.net.URLEncoder;

import butterknife.Bind;
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

    @Bind(R.id.jd_search_webview)
    WebView myJdWebView;

    @Override
    protected int getRootLayoutId() {
        return R.layout.view_jd_search;
    }

    @Override
    protected void initView() {
        myJdWebView.getSettings().setJavaScriptEnabled(true);
        myJdWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        myJdWebView.getSettings().setDomStorageEnabled(true);
        myJdWebView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        myJdWebView.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题
        //myJdWebView.requestFocusFromTouch();
        myJdWebView.getSettings().setAllowContentAccess(true);
        myJdWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            myJdWebView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }
        myJdWebView.setWebChromeClient(new WebChromeClient());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            myJdWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(myJdWebView, true);

        myJdWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    String replaceUrl = url.replace("https://", "").replace("http://", "");
                    if(replaceUrl.startsWith(jd_search_url_prifix)){
                        view.loadUrl(url);
                        return true;
                    }
                    //去到京东详情页处理
                    if(isPageGoodDetail(url)){
                         JdWebviewDialogFragment.newInstance(url).show(getFragmentManager(),"JdWebviewDialogFragment");
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

            @Override
            public void onLoadResource(WebView view, String url) {
                if(url.startsWith("http://stat.m.jd.com/m/access")){
                    System.out.println("333333");
                }
                super.onLoadResource(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                System.out.println("1111111");
                super.onReceivedSslError(view, handler, error);
            }
        });
        if(!isInit){
            myJdWebView.loadUrl(getCurrentUrl(this.q));
        }
    }

    @Override
    protected void initData() {

    }

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
