package cn.fuyoushuo.fqbb.domain.httpservice;

import com.alibaba.fastjson.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by QA on 2016/7/11.
 */
public interface TaoBaoSuggestHttpService {

     @GET("/sug?area=sug_hot")
     Observable<JSONObject> getTaobaoHotWords();

     @GET("/sug?code=utf-8")
     Observable<JSONObject> getTaobaoSuggestWords(@Query("q") String q);
}
