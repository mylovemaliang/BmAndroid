package cn.fuyoushuo.fqbb.domain.httpservice;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by QA on 2016/7/11.
 */
public interface AlimamaHttpService {

   //普通返利信息获取
   @GET("/items/search.json")
   Observable<JSONObject> getFanliInfo(@Query("q") String qUrl);

   //高返信息获取
   @GET("/items/channel/qqhd.json")
   Observable<JSONObject> getHdFanliInfo(@Query("q") String qUrl);

   //高返商品搜索
   @GET("/items/channel/qqhd.json")
   Observable<JSONObject> searchHdFanli(@QueryMap Map<String,String> queryMap);

   //普通返利搜索
   @GET("/items/search.json")
   Observable<JSONObject> searchFanli(@QueryMap Map<String,String> queryMap);

}
