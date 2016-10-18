package cn.fuyoushuo.fqbb.domain.httpservice;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by QA on 2016/7/11.
 */
public interface TaoBaoSearchHttpService {

     @GET("/search")
     Observable<JSONObject> getTaoBaoGoodsByCondition(@QueryMap Map<String,String> queryMap);
}
