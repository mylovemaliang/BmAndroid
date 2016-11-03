package cn.fuyoushuo.fqbb.presenter.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.Constants;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.commonlib.utils.MD5;
import cn.fuyoushuo.fqbb.commonlib.utils.UserInfoStore;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.view.view.JdGoodDetailView;
import cn.fuyoushuo.fqbb.view.view.login.LoginOriginView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/1.
 */
public class JdGoodDetailPresenter extends BasePresenter{

    public static final String VOLLEY_TAG_NAME = "JdGoodDetailPresenter";

    //view 引用
    private WeakReference<JdGoodDetailView> jdGoodDetailView;

    public JdGoodDetailPresenter(JdGoodDetailView jdGoodDetailView) {
        this.jdGoodDetailView = new WeakReference<JdGoodDetailView>(jdGoodDetailView);
    }

    //获取 VIEW 实例
    private JdGoodDetailView getMyView(){
        return jdGoodDetailView.get();
    }


    //获取京东的返利信息
    public void getJdFanliInfo(String itemId){

       mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
               .getJdFanliInfo(itemId)
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<HttpResp>() {
                   @Override
                   public void onCompleted() {

                   }

                   @Override
                   public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onGetJdFanliFail();
                       }
                   }

                   @Override
                   public void onNext(HttpResp httpResp) {
                       if(httpResp == null || httpResp.getS() != 1){
                           if(getMyView() != null){
                               getMyView().onGetJdFanliFail();
                           }
                       }else{
                           if(getMyView() != null){
                               Object result = httpResp.getR();
                               JSONArray resultArray = new JSONArray((List<Object>) result);
                               if(!resultArray.isEmpty()){
                                   if(getMyView() != null){
                                       getMyView().onGetJdFanliSucc(resultArray.getJSONObject(0));
                                   }
                               }
                           }
                       }
                   }
               })
       );
    }

    /**
     * 获取京东CPS链接
     * @param url
     */
    public void getJdCpsUrl(String url){
        if(TextUtils.isEmpty(url)){
            return;
        }
        try {
            url = URLEncoder.encode(url,"utf-8");
        } catch (UnsupportedEncodingException e){

        }
        mSubscriptions.add(createCpsUrlObservable(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(getMyView() != null){
                            getMyView().onGetCpsFail();
                        }
                    }

                    @Override
                    public void onNext(String s) {
                         if(TextUtils.isEmpty(s)){
                             if(getMyView() != null){
                                  getMyView().onGetCpsFail();
                             }
                         }else{
                             if(getMyView() != null){
                                  getMyView().onGetCpsSucc(s);
                             }
                         }
                    }
                })
        );
    }


    private Observable<String> createCpsUrlObservable(final String encodedUrl){
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                StringBuilder cpsUrl = new StringBuilder();
                cpsUrl.append(Constants.ENDPOINT_FQBB_LOCAL+"/mall/mjdcps.htm?");
                cpsUrl.append("d="+encodedUrl+"&");
                cpsUrl.append("l=1&");
                UserInfoStore userInfoStore = LoginInfoStore.getIntance().getUserInfoStore();
                if(userInfoStore == null){
                     subscriber.onNext("");
                }
                if(!TextUtils.isEmpty(userInfoStore.getSessionId()) && !TextUtils.isEmpty(userInfoStore.getUserId()) && !TextUtils.isEmpty(userInfoStore.getToken())){
                    cpsUrl.append("sessionid="+userInfoStore.getSessionId()+"&");
                    cpsUrl.append("t="+currentTime+"&");
                    String originText = userInfoStore.getSessionId()+userInfoStore.getUserId()+userInfoStore.getToken()+currentTime;
                    String vt = MD5.MD5Encode(originText);
                    cpsUrl.append("vt="+vt);
                    subscriber.onNext(cpsUrl.toString());
                }
            }
        });
    }




}
