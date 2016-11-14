package cn.fuyoushuo.fqbb.presenter.impl;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.fuyoushuo.fqbb.ServiceManager;
import cn.fuyoushuo.fqbb.domain.entity.TaoBaoItemVo;
import cn.fuyoushuo.fqbb.domain.entity.TbCateVo;
import cn.fuyoushuo.fqbb.domain.httpservice.AlimamaHttpService;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by QA on 2016/11/10.
 */
public class SelectedGoodPresenter extends BasePresenter{

     //特价好货
     public static final String TEHUI_CHANNEL = "tehui";

     //女装尖货
     public static final String NZJH_CHANNEL = "nzjh";

     //ifashion-流行男装
     public static final String IFI_CHANNEL = "ifs";

     //淘宝汇吃
     public static final String HCH_CHANNEL = "hch";

     //极有家
     public static final String JYJ_CHANNEL = "jyj";

     //酷动城
     public static final String KDC_CHANNEL = "kdc";

     //九块九
     public static final String JKJ_CHANNEL = "9k9";


      //获取精选商品
      public void  getSelectedGood(String channel,int toPage,String catIds,Integer level,final SelectGoodGetCallBack selectGoodGetCallBack){
          if(TextUtils.isEmpty(channel)){
                 selectGoodGetCallBack.onGetGoodFail("没有查到相关商品");
                 return;
          }
          mSubscriptions.add(ServiceManager.createService(AlimamaHttpService.class)
                  .searchSelectedGood(channel,channel,toPage,catIds,level)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new Subscriber<JSONObject>() {
                      @Override
                      public void onCompleted() {

                      }

                      @Override
                      public void onError(Throwable e) {
                          selectGoodGetCallBack.onGetGoodFail("查询出错,请重试");
                      }

                      @Override
                      public void onNext(JSONObject jsonObject) {
                          List<TaoBaoItemVo> resultList = new ArrayList<TaoBaoItemVo>();
                          LinkedList<TbCateVo> cateList = new LinkedList<TbCateVo>();
                          parseAlimamaCommonGoodsList(jsonObject,resultList);
                          parseAlimamaGoodCateList(jsonObject,cateList);
                          selectGoodGetCallBack.onGetGoodSucc(resultList,cateList);
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

    //获取阿里妈妈的一级子类目信息
    private void parseAlimamaGoodCateList(JSONObject resultObject, LinkedList<TbCateVo> catelist){
        if(resultObject == null || resultObject.isEmpty()){
            return;
        }
        if(catelist == null){
            return;
        }
        JSONObject data = resultObject.getJSONObject("data");
        if(data == null || data.isEmpty()) return;
        JSONArray navigator = data.getJSONArray("navigator");
        if(navigator == null || navigator.isEmpty()) return;
        JSONObject firstCate = navigator.getJSONObject(0);
        if("相关类目".equals(firstCate.getString("name"))){
            JSONArray subIds = firstCate.getJSONArray("subIds");
            if(subIds == null || subIds.isEmpty()) return;
            for(int i = 0;i<subIds.size();i++){
                JSONObject item = subIds.getJSONObject(i);
                String catId = String.valueOf(item.getLongValue("id"));
                String catName = item.getString("name");
                int level = item.getInteger("level");
                catelist.add(new TbCateVo(catId,level,catName));
            }
        }else{
            for(int j = 0;j<navigator.size();j++){
                JSONObject item = navigator.getJSONObject(j);
                String catId = String.valueOf(item.getLongValue("id"));
                String catName = item.getString("name");
                int level = item.getInteger("level");
                catelist.add(new TbCateVo(catId,level,catName));
            }
        }
    }

    private String handlerTitle(String title){
            if(TextUtils.isEmpty(title)){
                return title;
            }
            title = title.replaceAll("<.*?>","");
            return title;
    }

    //获取精选商品回调
      public interface SelectGoodGetCallBack{

          void onGetGoodSucc(List<TaoBaoItemVo> goodList,LinkedList<TbCateVo> cateList);

          void onGetGoodFail(String msg);

      }

}
