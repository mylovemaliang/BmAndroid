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

    @GET("/user/doregist.htm?regFrom=2")
    Observable<HttpResp> registerUser(@Query("phone") String phoneNum,@Query("password") String password,@Query("mobileCode") String mobileCode);

    @GET("/user/mlogin.htm")
    Observable<HttpResp> userLogin(@Query("loginid") String loginid,@Query("password") String passwordMD5);

    @GET("/user/mlu.htm")
    Observable<HttpResp> getUserInfo();

    @GET("/mall/mjdrate.htm")
    Observable<HttpResp> getJdFanliInfo(@Query("itemid") String itemId);

    @GET("/point/getSkus.htm?itemId=1")
    Observable<HttpResp> getPhoneRechargeSkus();

    @GET("/point/mpbuy.htm?itemId=1")
    Observable<HttpResp> createPhoneRechargeOrder(@Query("skuId") Long skuId,@Query("diliverPhone") String phoneNum);

    @GET("/user/doFindPwd.htm")
    Observable<HttpResp> findUserPass(@Query("eop") String accountValue,@Query("code") String verifiCode,@Query("password") String newpass);

    @GET("/user/mlogout.htm")
    Observable<HttpResp> userLogout();

    @GET("/user/mdoBindEmail.htm")
    Observable<HttpResp> bindEmail(@Query("email") String email,@Query("code") String verifiCode);

    @GET("/user/mdoUnBindEmail.htm")
    Observable<HttpResp> unbindEmail(@Query("eop") String account,@Query("code") String code);

    @GET("/user/validePhone.htm")
    Observable<HttpResp> validePhone(@Query("phone") String phoneNum);

    @GET("/user/valideEmail.htm")
    Observable<HttpResp> valideEmail(@Query("email") String email);

    @GET("/user/mUpdatePwd.htm")
    Observable<HttpResp> updatePassword(@Query("currentPwd") String originPassword,@Query("newPassword") String newPassword);

    //获取兑换订单
    @GET("/point/morder-{pagenum}.htm")
    Observable<HttpResp> getDhOrders(@Path("pagenum") int pageNum,@Query("queryStatus") Integer queryStatus);

    //获取兑换详情
    @GET("/point/mpdetail-{pagenum}.htm?pagesize=50")
    Observable<HttpResp> getDhDetails(@Path("pagenum") int pageNum);

}


