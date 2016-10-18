package cn.fuyoushuo.fqbb.domain.httpservice;

import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by maliang on 2016/6/30.
 */
public interface FqbbHttpService {

    @GET("/item/cates2.htm")
    Observable<HttpResp> getCates();

    @GET("/item/listitemsInter/{cateId}.htm")
    Observable<HttpResp> getGoodItems(@Path("cateId") Long cateId, @Query("pagesize") Integer pageSize, @Query("pageno") Integer pageNo);

}
