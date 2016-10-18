package cn.fuyoushuo.fqbb.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.fuyoushuo.fqbb.R;

public class HelpActivity extends BaseActivity {

    WebView helpWebview;

    LinearLayout helpBackRl;

    ImageView helpWebviewBackImg;

    boolean howToTixian = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_help);

        helpBackRl = (LinearLayout) this.findViewById(R.id.helpBackRl);
        helpWebviewBackImg = (ImageView) this.findViewById(R.id.helpWebviewBackImg);
        helpWebview = (WebView) this.findViewById(R.id.helpWebview);

        helpBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        helpWebviewBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        helpWebview.getSettings().setJavaScriptEnabled(true);
        helpWebview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        helpWebview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        helpWebview.getSettings().setDomStorageEnabled(true);

        helpWebview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        helpWebview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        helpWebview.requestFocusFromTouch();
        helpWebview.setWebChromeClient(new WebChromeClient());

        helpWebview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("www://")){
                    return false;
                }else{
                    return true;
                }
            }
        });

        Intent in = getIntent();
        howToTixian = in.getBooleanExtra("howToTixian", false);
        if(howToTixian){
            helpWebview.loadUrl("http://www.fanqianbb.com/mfwq/fwq4.html");
        }else{
            helpWebview.loadUrl("http://www.fanqianbb.com/mfwq/fwqlist.html");
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    @Override
    protected void onDestroy() {
        if(helpWebview!=null){
            ViewGroup viewGroup = (ViewGroup) helpWebview.getParent();
            if(viewGroup!=null){
                viewGroup.removeView(helpWebview);
            }
            helpWebview.removeAllViews();
            helpWebview.destroy();
            helpWebview=null;
        }
        super.onDestroy();
    }

    public void goBack(){
        String currentUrl = helpWebview.getUrl();

        if(howToTixian){
            howToTixian = false;

            helpWebview.setVisibility(View.GONE);
            Intent intent = new Intent(HelpActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            this.finish();
        }

        if(currentUrl.contains("www.fanqianbb.com/mfwq") && !currentUrl.contains("www.fanqianbb.com/mfwq/fwqlist.html")){
            helpWebview.goBack();
            return;
        }else{
            helpWebview.setVisibility(View.GONE);
            Intent intent = new Intent(HelpActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            this.finish();
        }
    }
}
