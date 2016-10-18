package cn.fuyoushuo.fqbb.domain.httpservice;

import com.alibaba.fastjson.JSONObject;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by QA on 2016/7/12.
 */
public interface JifenBaoHttpService {

    @GET("/api/purchase_detail.do?src=auction_detail")
    Observable<JSONObject> getJfbFanliInfo(@Query("nid") String goodId);
}
