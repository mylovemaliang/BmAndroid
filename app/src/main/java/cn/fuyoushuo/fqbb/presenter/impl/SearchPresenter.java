package cn.fuyoushuo.fqbb.presenter.impl;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import cn.fuyoushuo.fqbb.domain.ext.SearchCondition;
import cn.fuyoushuo.fqbb.domain.httpservice.AlimamaHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.TaoBaoSearchHttpService;
import cn.fuyoushuo.fqbb.view.view.SearchView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/7/11.
 * 用于搜索的Presenter
 */
public class SearchPresenter extends BasePresenter{

    private WeakReference<SearchView> searchView;

    public SearchPresenter(SearchView searchView) {
        this.searchView = new WeakReference<SearchView>(searchView);
    }

    //可能为null,后续操作全部需要加上非NULL判断
    private SearchView getMyView(){
        return searchView.get();
    }

    //获取搜索结果
    public void getSearchResult(final SearchCondition searchCondition,final boolean isReflash){
       //最终结果
        final List<TaoBaoItemVo> resultList = new ArrayList<TaoBaoItemVo>();
        final Map<String, String> queryMap = searchCondition.getQueryMap();
        final String currentSearchCate = searchCondition.getCurrentSearchCate();
        //搜索逻辑统计
        if(!TextUtils.isEmpty(currentSearchCate) && SearchCondition.search_cate_taobao.equals(currentSearchCate)){
            if(getMyView() != null){
               MobclickAgent.onEvent(getMyView().getMyContext(),EventIdConstants.SEARCH_TYPE_TAOBAO);
            }
        }else if(!TextUtils.isEmpty(currentSearchCate) && SearchCondition.search_cate_commonfan.equals(currentSearchCate)){
            if(getMyView() != null){
                MobclickAgent.onEvent(getMyView().getMyContext(),EventIdConstants.SEARCH_TYPE_FANLI);
            }
        }else{
            if(getMyView() != null){
                MobclickAgent.onEvent(getMyView().getMyContext(),EventIdConstants.SEARCH_TYPE_SUPERFANLI);
            }
        }

        mSubscriptions.add(Observable.just(currentSearchCate)
                 .flatMap(new Func1<String, Observable<? extends Object>>() {
                     @Override
                     public Observable<?> call(String searchType) {
                         if (!SearchCondition.search_cate_superfan.equals(searchType)) {
                             return Observable.just(searchType);
                         }
                         Observable<JSONObject> resultJsonObject = ServiceManager.createService(AlimamaHttpService.class).searchHdFanli(queryMap);
                         return resultJsonObject;
                     }
                 })
                .flatMap(new Func1<Object, Observable<? extends Object>>() {
                    @Override
                    public Observable<? extends Object> call(Object o) {
                        if (o instanceof String) {
                            String searchType = String.valueOf(o);
                            if (!SearchCondition.search_cate_commonfan.equals(searchType)) {
                                return Observable.just(searchType);
                            }
                            Observable<JSONObject> resultJsonObject = ServiceManager.createService(AlimamaHttpService.class).searchFanli(queryMap);
                            return resultJsonObject;
                        } else {
                            return Observable.just((JSONObject) o);
                        }
                    }
                })
                .flatMap(new Func1<Object, Observable<? extends Object>>() {
                    @Override
                    public Observable<? extends Object> call(Object o) {
                        if (o instanceof String) {
                            String searchType = String.valueOf(o);
                            if (!SearchCondition.search_cate_taobao.equals(searchType)) {
                                return Observable.just(searchType);
                            }
                            Observable<JSONObject> resultJsonObject = ServiceManager.createService(TaoBaoSearchHttpService.class).getTaoBaoGoodsByCondition(queryMap);
                            return resultJsonObject;
                        } else {
                            return Observable.just((JSONObject) o);
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {


                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(getMyView() != null){
                          getMyView().setGoodsList(resultList, isReflash);
                          if(!isReflash){
                            getMyView().setAlertDialogIfNull();
                          }
                        }
                    }

                    @Override
                    public void onNext(Object o) {
                        if (o instanceof String) return;
                        JSONObject result = (JSONObject) o;
                        if (SearchCondition.search_cate_superfan.equals(currentSearchCate)) {
                            parseAlimamaHdGoodsList(result, resultList);
                        } else if (SearchCondition.search_cate_commonfan.equals(currentSearchCate)) {
                            parseAlimamaCommonGoodsList(result, resultList);
                        } else if (SearchCondition.search_cate_taobao.equals(currentSearchCate)) {
                            parseTaobaoGoodsList(result, resultList);
                        }
                        if(getMyView() != null){
                            getMyView().setGoodsList(resultList, isReflash);
                          if(resultList.isEmpty() && !isReflash){
                              getMyView().setAlertDialogIfNull();
                          }
                        }
                    }
                })
        );
    }

    //获取返利信息
    public void getDiscountInfo(final View fanliView,final TaoBaoItemVo taoBaoItemVo){
         String url = taoBaoItemVo.getUrl();
         final String itemId = taoBaoItemVo.getItem_id();
         if(TextUtils.isEmpty(url) || TextUtils.isEmpty(itemId)) {
             if(getMyView() != null){
                 getMyView().updateFanliInfo(fanliView,taoBaoItemVo,true);
             }
             return;
         }
         if(url.startsWith("//")){
             url = url.replaceFirst("//","https://");
         }
         //拼接Url
         url = getRealTbUrl(url,itemId);
         final String finalUrl = url;
         mSubscriptions.add(
                 ServiceManager.createService(AlimamaHttpService.class).getFanliInfo(finalUrl)
                         .doOnError(new Action1<Throwable>() {
                             @Override
                             public void call(Throwable throwable) {
                                 Log.d("getFanliInfo error",throwable.getMessage());
                             }
                         })
                         .onErrorResumeNext(new Func1<Throwable, Observable<JSONObject>>() {
                             @Override
                             public Observable<JSONObject> call(Throwable throwable) {
                                 JSONObject jsonObject = new JSONObject();
                                 jsonObject.put("error", true);
                                 return Observable.just(jsonObject);
                             }
                         }).flatMap(new Func1<JSONObject, Observable<? extends Object>>() {
                             @Override
                             public Observable<? extends Object> call(JSONObject resultObject) {
                                   if (resultObject != null && !resultObject.getBooleanValue("error")) {
                                         parseFanliInfo(resultObject, taoBaoItemVo);
                                         if (null != taoBaoItemVo.getTkRate()) {
                                             return Observable.just(taoBaoItemVo);
                                         }
                                     }
                                    return Observable.just(resultObject);
//                                  return ServiceManager.createService(JifenBaoHttpService.class).getJfbFanliInfo(itemId)
//                                         .doOnError(new Action1<Throwable>() {
//                                             @Override
//                                             public void call(Throwable throwable) {
//                                                 Log.d("getJfbFanliInfo error",throwable.getMessage());
//                                             }
//                                         }).onErrorResumeNext(new Func1<Throwable, Observable<? extends JSONObject>>() {
//                                             @Override
//                                             public Observable<? extends JSONObject> call(Throwable throwable) {
//                                                 JSONObject errorObject = new JSONObject();
//                                                 errorObject.put("error", true);
//                                                 return Observable.just(errorObject);
//                                             }
//                                         });
                             }
                         })
                         .flatMap(new Func1<Object, Observable<TaoBaoItemVo>>() {

                             @Override
                             public Observable<TaoBaoItemVo> call(Object o) {
                                 if (o instanceof TaoBaoItemVo) {
                                     return Observable.just((TaoBaoItemVo) o);
                                 }
//                                 if (o instanceof JSONObject) {
//                                     JSONObject resultObject = (JSONObject) o;
//                                     if (resultObject != null && !resultObject.getBooleanValue("error")) {
//                                         parseJfbFanliInfo(resultObject,taoBaoItemVo);
//                                     }
//                                 }
                                 return Observable.just(taoBaoItemVo);
                             }
                         })
                         .subscribeOn(Schedulers.io())
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(new Subscriber<TaoBaoItemVo>() {
                             @Override
                             public void onCompleted() {
                                 // TODO: 2016/7/12
                             }

                             @Override
                             public void onError(Throwable e) {
                                 if(getMyView() != null) {
                                     getMyView().updateFanliInfo(fanliView, taoBaoItemVo, true);
                                 }
                             }

                             @Override
                             public void onNext(TaoBaoItemVo taoBaoItemVo) {
                                 if(getMyView() != null) {
                                     getMyView().updateFanliInfo(fanliView, taoBaoItemVo, false);
                                 }
                             }
                         })

         );
    }

    //解析阿里妈妈搜索结果
    private void parseAlimamaCommonGoodsList(JSONObject resultObject,List<TaoBaoItemVo> resultList){
        if(resultObject == null || resultObject.isEmpty()){
            return;
        }
        if(resultList == null){
            return;
        }
        JSONObject data = resultObject.getJSONObject("data");
        if(data == null || data.isEmpty()) return;
        JSONArray pageList = data.getJSONArray("pageList");
        if(pageList == null || pageList.isEmpty()) return;
        for(Object item : pageList){
            JSONObject jsonObject = new JSONObject((Map<String, Object>) item);
            TaoBaoItemVo taoBaoItemVo = new TaoBaoItemVo();
            taoBaoItemVo.setFanliSearched(true);
            String title = jsonObject.getString("title");
            taoBaoItemVo.setTitle(handlerTitle(title));
            taoBaoItemVo.setPic_path(jsonObject.getString("pictUrl"));
            taoBaoItemVo.setSold(jsonObject.getInteger("biz30day").toString());
            taoBaoItemVo.setPrice(jsonObject.getDouble("zkPrice").toString());
            taoBaoItemVo.setUrl(jsonObject.getString("auctionUrl"));
            taoBaoItemVo.setTkRate(jsonObject.getFloat("tkRate"));
            taoBaoItemVo.setTkCommFee(jsonObject.getFloat("tkCommFee"));
            resultList.add(taoBaoItemVo);
        }
    }

    //解析阿里妈妈高返搜索结果
    private void parseAlimamaHdGoodsList(JSONObject resultObject,List<TaoBaoItemVo> resultList){
        if(resultObject == null || resultObject.isEmpty()){
            return;
        }
        if(resultList == null){
            return;
        }
        JSONObject data = resultObject.getJSONObject("data");
        if(data == null || data.isEmpty()) return;
        JSONArray pageList = data.getJSONArray("pageList");
        if(pageList == null || pageList.isEmpty()) return;
        for(Object item : pageList){
            JSONObject jsonObject = new JSONObject((Map<String, Object>) item);
            TaoBaoItemVo taoBaoItemVo = new TaoBaoItemVo();
            taoBaoItemVo.setFanliSearched(true);
            String title = jsonObject.getString("title");
            taoBaoItemVo.setTitle(handlerTitle(title));
            taoBaoItemVo.setPic_path(jsonObject.getString("pictUrl"));
            taoBaoItemVo.setSold(jsonObject.getInteger("biz30day").toString());
            taoBaoItemVo.setPrice(jsonObject.getDouble("zkPrice").toString());
            taoBaoItemVo.setUrl(jsonObject.getString("auctionUrl"));
            taoBaoItemVo.setTkRate(jsonObject.getFloat("eventRate"));
            taoBaoItemVo.setTkCommFee(jsonObject.getFloat("tkCommFee"));
            resultList.add(taoBaoItemVo);
        }
    }

    //解析淘宝搜索结果
    private void parseTaobaoGoodsList(JSONObject resultObject,List<TaoBaoItemVo> resultList){
        if(resultObject == null || resultObject.isEmpty()){
            return;
        }
        if(resultList == null){
            return;
        }
        JSONArray items = resultObject.getJSONArray("listItem");
        if(items == null || items.isEmpty()) return;
        for(Object item : items){
            JSONObject jsonObject = new JSONObject((Map<String, Object>) item);
            TaoBaoItemVo taoBaoItemVo = jsonObject.toJavaObject(TaoBaoItemVo.class);
            taoBaoItemVo.setFanliSearched(false);
            resultList.add(taoBaoItemVo);
        }
    }

    //获取组装URL
    private String getRealTbUrl(String url,String itemId){
        if(TextUtils.isEmpty(url)){
            return "";
        }
        StringBuffer finalUrl = new StringBuffer();
        finalUrl.append("https://");
        if(url.indexOf("taobao.com")>-1){
             finalUrl.append("item.taobao.com");
        } else if(url.indexOf("tmall.com")>-1){
             finalUrl.append("detail.tmall.com");
        }
        finalUrl.append("/item.htm?id="+itemId);
        return finalUrl.toString();
    }
    //获取返利信息
    private void parseFanliInfo(JSONObject resultObject,TaoBaoItemVo vo){
        if(resultObject == null || resultObject.isEmpty()){
            return;
        }
        JSONObject data = resultObject.getJSONObject("data");
        if(data == null || data.isEmpty()) return;
        JSONArray pageList = data.getJSONArray("pageList");
        if(pageList == null || pageList.isEmpty()) return;
        JSONObject fanliInfo = pageList.getJSONObject(0);

        Float itemPrice = null;
        Float gfRate = fanliInfo.getFloat("eventRate");
        Float gfFeeCount = null;

        Float fxRate = fanliInfo.getFloat("tkRate");
        Float fxFee = fanliInfo.getFloat("tkCommFee");

        if(gfRate != null && gfRate > 0){
            itemPrice = fanliInfo.getFloat("zkPrice");
            if(itemPrice == null){
                itemPrice = fanliInfo.getFloat("reservePrice");
            }
            gfFeeCount = itemPrice*gfRate/100;
        }
        if(gfFeeCount != null ){
          vo.setTkRate(gfRate);
          vo.setTkCommFee(gfFeeCount);
        }else if(fxFee != null){
          vo.setTkRate(fxRate);
          vo.setTkCommFee(fxFee);
        }
        vo.setFanliSearched(true);
    }
    //获取高返信息
    private void parseHdFanliInfo(JSONObject result,TaoBaoItemVo vo){
        List<TaoBaoItemVo> resultList = new ArrayList<TaoBaoItemVo>();
        parseAlimamaHdGoodsList(result,resultList);
        if(resultList.isEmpty()) return;
        TaoBaoItemVo taoBaoItemVo = resultList.get(0);
        vo.setTkRate(taoBaoItemVo.getTkRate());
        vo.setTkCommFee(taoBaoItemVo.getTkCommFee());
    }

    //获取集分宝信息
    private void parseJfbFanliInfo(JSONObject result,TaoBaoItemVo vo){
        if(!result.getBoolean("success")) return;
        JSONObject resultObject = result.getJSONObject("result");
        if(resultObject == null || resultObject.isEmpty()) return;
        JSONObject bestPlan = resultObject.getJSONObject("bestPlan");
        if(bestPlan == null || bestPlan.isEmpty()) return;
        Integer rebateSaving = bestPlan.getInteger("rebateSaving");
        if(rebateSaving != null){
            vo.setJfbCount(rebateSaving);
        }
    }

    private String handlerTitle(String title){
        if(TextUtils.isEmpty(title)){
            return title;
        }
        title = title.replaceAll("<.*?>","");
        return title;
    }

}
