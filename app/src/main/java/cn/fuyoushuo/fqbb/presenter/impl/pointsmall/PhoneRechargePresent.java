package cn.fuyoushuo.fqbb.presenter.impl.pointsmall;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.DataCheckUtils;
import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbLocalHttpService;
import cn.fuyoushuo.fqbb.presenter.impl.BasePresenter;
import cn.fuyoushuo.fqbb.view.view.pointsmall.PhoneRechargeView;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/7.
 */
public class PhoneRechargePresent extends BasePresenter{


    private WeakReference<PhoneRechargeView> phoneRechargeView;

    public PhoneRechargePresent(PhoneRechargeView phoneRechargeView) {
        this.phoneRechargeView = new WeakReference<PhoneRechargeView>(phoneRechargeView);
    }

    //可能为null,后续操作全部需要加上非NULL判断
    private PhoneRechargeView getMyView(){
        return phoneRechargeView.get();
    }


    public void getPhoneRechargeSkus(){

        mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
                .getPhoneRechargeSkus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                       if(getMyView() != null){
                           getMyView().onPhoneRechargeSkuGetFail();
                       }
                    }

                    @Override
                    public void onNext(HttpResp httpResp) {
                        if(httpResp == null || httpResp.getS() != 1){
                            if(getMyView() != null){
                                getMyView().onPhoneRechargeSkuGetFail();
                            }
                        }else{
                            Object r = httpResp.getR();
                            JSONArray arrays = new JSONArray((List<Object>) r);
                            if(arrays != null && !arrays.isEmpty()){
                                List<JSONObject> results = new ArrayList<JSONObject>();
                                for(int i = 0;i < arrays.size();i++){
                                    results.add(arrays.getJSONObject(i));
                                }
                                if(getMyView() != null){
                                    getMyView().onPhoneRechargeSkuGetSucc(results);
                                }
                            }
                        }
                    }
                })
        );
    }

    /**
     * 创建积分兑换话费订单
     * @param skuId 充值的商品skuid
     * @param phoneNum 需要充值的手机号码
     */
    public void createPhoneRechargeOrder(Long skuId,String phoneNum){
         if(skuId == null || TextUtils.isEmpty(phoneNum) || !DataCheckUtils.isPhoneLegal(phoneNum)){
             if(getMyView() != null){
                 getMyView().onPhoneRechargeFail("数据校验失败");
             }
             return;
         }
         mSubscriptions.add(ServiceManager.createService(FqbbLocalHttpService.class)
             .createPhoneRechargeOrder(skuId,phoneNum)
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(new Subscriber<HttpResp>() {
                 @Override
                 public void onCompleted() {

                 }

                 @Override
                 public void onError(Throwable e) {
                    if(getMyView() != null){
                        getMyView().onPhoneRechargeFail("充值失败,请稍后再试");
                    }
                 }

                 @Override
                 public void onNext(HttpResp httpResp) {
                     if(httpResp == null || httpResp.getS() != 1){
                         if(getMyView() != null){
                             getMyView().onPhoneRechargeFail("充值失败,请稍后再试");
                         }
                     }else{
                         if(getMyView() != null){
                             getMyView().onPhoneRechargeSucc();
                         }
                     }
                 }
             })
         );
    }


}
