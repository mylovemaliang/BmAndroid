package cn.fuyoushuo.fqbb.domain.httpservice;

import cn.fuyoushuo.fqbb.domain.ext.HttpResp;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by maliang on 2016/6/30.
 */
public interface FqbbLocalHttpService {

    @GET("/vcc/sc.htm")
    Observable<HttpResp> getVerifiCode(@Query("vct") String vct,@Query("eop") String eop);

    @GET("/user/doregist.htm")
    Observable<HttpResp> registerUser(@Query("phone") String phoneNum,@Query("password") String password,@Query("mobileCode") String mobileCode);

    @GET("/user/mlogin.htm")
    Observable<HttpResp> userLogin(@Query("loginid") String loginid,@Query("password") String passwordMD5);

    @GET("/user/mlu.htm")
    Observable<HttpResp> getUserInfo();

    @GET("/mall/mjdrate.htm")
    Observable<HttpResp> getJdFanliInfo(@Query("itemid") String itemId);
}


