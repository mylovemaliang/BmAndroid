package cn.fuyoushuo.fqbb.presenter.impl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.CommonUtils;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.TaobaoInterPresenter.LoginCallback;
import cn.fuyoushuo.fqbb.view.flagment.SearchPromptFragment;
import cn.fuyoushuo.fqbb.view.view.UserCenterView;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/1.
 */
public class UserCenterPresenter extends BasePresenter{

    public static final String VOLLEY_TAG_NAME = "UserCenterPresenter";

    //view 引用
    private WeakReference<UserCenterView> userCenterView;

    public UserCenterPresenter(UserCenterView userCenterView) {
        this.userCenterView = new WeakReference<UserCenterView>(userCenterView);
    }

    //获取 VIEW 实例
    private UserCenterView getMyView(){
        return userCenterView.get();
    }

    //获取当前用户信息
    public void getUserInfo(){
        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
               .getUserInfo()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<HttpResp>() {
                   @Override
                   public void onCompleted() {

                   }

                   @Override
                   public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onUserInfoGetError();
                       }
                   }

                   @Override
                   public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onLoginFail();
                           }
                       }else {
                           if(getMyView() != null){
                               JSONObject result = new JSONObject(((Map)(httpResp.getR())));
                               result.put("account", LoginInfoStore.getIntance().getUserAccount());
                               getMyView().onUserInfoGetSucc(result);
                           }
                       }
                   }
               })
        );}


    public void logout(){
         mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
            .userLogout()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<HttpResp>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                   if(getMyView() != null){
                       getMyView().onLogoutFail();
                   }
                }

                @Override
                public void onNext(HttpResp httpResp) {
                    if(httpResp == null || httpResp.getS() != 1){
                        if(getMyView() != null){
                            getMyView().onLogoutFail();
                        }
                    }else{
                        LoginInfoStore.getIntance().clearUserInfo();
                        if(getMyView() != null){
                            getMyView().onLogoutSuccess();
                        }
                    }
                }
            })
         );
    }


    //获取阿里妈妈信息
    public void getAlimamaInfo(){

       TaobaoInterPresenter.judgeAlimamaLogin(new LoginCallback(){
           @Override
           public void hasLoginCallback() {
               mSubscriptions.add(createAlimamaInfoObservable()
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .subscribe(new Subscriber<JSONObject>() {
                           @Override
                           public void onCompleted() {

                           }

                           @Override
                           public void onError(Throwable e) {
                               if(getMyView() != null){
                                   getMyView().onAlimamaLoginError();
                               }
                           }

                           @Override
                           public void onNext(JSONObject jsonObject) {
                               if(getMyView() != null){
                                   getMyView().onAlimamaLoginSuccess(jsonObject);
                               }
                           }
                       })
               );
           }

           @Override
           public void nologinCallback() {
                 if(getMyView() != null){
                     getMyView().onAlimamaLoginFail();
                 }
           }

           @Override
           public void judgeErrorCallback() {
                 if(getMyView() != null){
                     getMyView().onAlimamaLoginError();
                 }
           }
        },VOLLEY_TAG_NAME);
    }

    private Observable<JSONObject> createAlimamaInfoObservable(){
        return Observable.create(new Observable.OnSubscribe<JSONObject>() {
            @Override
            public void call(final Subscriber<? super JSONObject> subscriber) {
                final JSONObject result = new JSONObject();
                String url = "http://media.alimama.com/account/overview.htm";
                RequestQueue volleyRequestQueue = TaobaoInterPresenter.getVolleyRequestQueue();
                StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Document document = Jsoup.parse(response);
                                if(document == null) {
                                    subscriber.onNext(result);
                                    return;
                                }
                                Elements icomes = document.getElementsByClass("income-wrap");
                                if(icomes == null || icomes.isEmpty()){
                                    subscriber.onNext(result);
                                    return;
                                }
                                Elements selects = icomes.select("span.money");
                                if(selects == null || selects.isEmpty()){
                                    subscriber.onNext(result);
                                    return;
                                }
                                if(selects.size() == 4){
                                     result.put("lastDayMoney",selects.get(0).text());
                                     result.put("thisMonthMoney",selects.get(1).text());
                                     result.put("lastMonthMoney",selects.get(2).text());
                                     result.put("currentMoney",selects.get(3).text());
                                }
                                subscriber.onNext(result);
                            }},
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                subscriber.onNext(result);
                            }
                        });
                 stringRequest.setTag(VOLLEY_TAG_NAME);
                 volleyRequestQueue.add(stringRequest);
            }
        });
    }

    @Override
    public void onDestroy() {
        TaobaoInterPresenter.cancelTagedRuquests(VOLLEY_TAG_NAME);
        super.onDestroy();
    }
}
