package cn.fuyoushuo.fqbb.view.flagment.order;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.FragmentEvent;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.R;
import cn.fuyoushuo.fqbb.presenter.impl.LocalLoginPresent;
import cn.fuyoushuo.fqbb.presenter.impl.LocalOrderPresenter;
import cn.fuyoushuo.fqbb.view.Layout.SearchPointsOrderMenu;
import cn.fuyoushuo.fqbb.view.activity.UserLoginActivity;
import cn.fuyoushuo.fqbb.view.adapter.SearchMenuAdapter;
import cn.fuyoushuo.fqbb.view.flagment.BaseFragment;
import cn.fuyoushuo.fqbb.view.view.LocalOrderView;
import rx.functions.Action1;

/**
 * Created by QA on 2016/11/4.
 */
public class LocalOrderFragment extends BaseFragment implements LocalOrderView{

    @Bind(R.id.local_order_all)
    TextView allOrderText;

    @Bind(R.id.local_order_allstate)
    TextView allOrderStateText;

    @Bind(R.id.local_order_last30Day)
    TextView last30DayText;

    @Bind(R.id.local_myorder_webview_area)
    RelativeLayout webViewArea;

    private WebView orderWebview;

    private boolean isAll = true;

    private boolean isLast30Day = false;

    private String status = "";

    //条件筛选界面
    private SearchPointsOrderMenu searchPointsOrderMenu;

    private LocalOrderPresenter localOrderPresenter;

    private LocalLoginPresent localLoginPresent;

    @Override
    protected int getRootLayoutId() {
        return R.layout.fragment_local_myorder;
    }


    @Override
    protected void initView(View inflateView) {
        super.initView(inflateView);
        searchPointsOrderMenu = new SearchPointsOrderMenu(mactivity,webViewArea).init();

        orderWebview = new WebView(MyApplication.getContext());
        orderWebview.getSettings().setJavaScriptEnabled(true);
        //myWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        orderWebview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        orderWebview.getSettings().setDomStorageEnabled(true);

        orderWebview.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放。大视图模式
        orderWebview.getSettings().setLoadWithOverviewMode(true);// 和setUseWideViewPort(true)一起解决网页自适应问题

        orderWebview.requestFocusFromTouch();
        orderWebview.setWebChromeClient(new WebChromeClient());

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            orderWebview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CookieManager.getInstance().setAcceptThirdPartyCookies(orderWebview, true);
        }

        orderWebview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webViewArea.addView(orderWebview);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchPointsOrderMenu.setOnItemClick(new SearchPointsOrderMenu.OnItemClick() {
            @Override
            public void onclick(View view, SearchMenuAdapter.RowItem rowItem) {
                allOrderStateText.setText(rowItem.getRowDesc());
                status = rowItem.getSortCode();
                isAll = false;
                allOrderText.setTextColor(getResources().getColor(R.color.black));
                loadWebview();
                searchPointsOrderMenu.dismissWindow();
            }
        });

        RxView.clicks(allOrderText).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAll = true;
                        status = "";
                        isLast30Day = false;
                        last30DayText.setTextColor(getResources().getColor(R.color.black));
                        allOrderText.setTextColor(getResources().getColor(R.color.module_11));
                        allOrderStateText.setText("全部状态");
                        loadWebview();
                    }
                });

        RxView.clicks(last30DayText).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        isAll = false;
                        isLast30Day = !isLast30Day;
                        if(isLast30Day){
                            last30DayText.setTextColor(getResources().getColor(R.color.module_11));
                        }else{
                            last30DayText.setTextColor(getResources().getColor(R.color.black));
                        }
                        loadWebview();
                    }
                });

        RxView.clicks(allOrderStateText).compose(this.<Void>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .throttleFirst(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        searchPointsOrderMenu.showWindow();
                    }
                });

        allOrderText.setTextColor(getResources().getColor(R.color.module_11));
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if(orderWebview != null){
            orderWebview.removeAllViews();
            orderWebview.destroy();
        }
        localOrderPresenter.onDestroy();
        if(searchPointsOrderMenu != null){
            searchPointsOrderMenu.dismissWindow();
        }

    }

    @Override
    protected void initData() {
        localOrderPresenter = new LocalOrderPresenter(this);
        localLoginPresent = new LocalLoginPresent();
    }


    public static LocalOrderFragment newInstance() {
        LocalOrderFragment fragment = new LocalOrderFragment();
        return fragment;
    }

    public void loadWebview(){
        localLoginPresent.isFqbbLocalLogin(new LocalLoginPresent.LoginCallBack() {
            @Override
            public void localLoginSuccess() {
                localOrderPresenter.getloadUrl(isAll,status,isLast30Day);
            }

            @Override
            public void localLoginFail() {
                Intent intent = new Intent(getActivity(), UserLoginActivity.class);
                intent.putExtra("biz","MainToLocalOrder");
                startActivity(intent);
            }
        });
    }

    //---------------------------------------view层回调------------------------------------------------

    @Override
    public void onGetLoadUrlSucc(String loadUrl) {
        if(orderWebview != null){
            orderWebview.loadUrl(loadUrl);
        }
    }

    @Override
    public void onGetLoadUrlFail() {

    }
}
