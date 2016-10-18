package cn.fuyoushuo.fqbb;

import android.content.Context;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import cn.fuyoushuo.fqbb.commonlib.utils.Constants;
import cn.fuyoushuo.fqbb.domain.httpservice.AlimamaHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.FqbbHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.JifenBaoHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.TaoBaoSearchHttpService;
import cn.fuyoushuo.fqbb.domain.httpservice.TaoBaoSuggestHttpService;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @Project CommonProject
 * @Packate com.micky.commonproj.domain.service
 *
 * @Description Retrofit 服务管理
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-22 14:43
 * @Version 1.0
 */
public class ServiceManager {

    private static HashMap<String, Object> mServiceMap = new HashMap<String, Object>();

    public static Context context = MyApplication.getContext();

    /**
     *  创建Retrofit Service
     * @param t Service类型
     * @param <T>
     * @return
     */
    public static <T> T createService(Class<T> t) {
        T service = (T) mServiceMap.get(t.getName());

        if (service == null) {
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

            if (Constants.DEBUG) {
                //日志处理
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String s) {
                       Logger.getLogger(getClass()).debug(s);
                    }
                });
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                clientBuilder.addInterceptor(loggingInterceptor);
            }


            //缓存处理
            final File baseDir = context.getCacheDir();
            if (baseDir != null) {
                final File cacheDir = new File(baseDir, "HttpResponseCache");
                clientBuilder.cache(new Cache(cacheDir, Constants.HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
            }
            clientBuilder.interceptors().add(new ServiceInterceptor());

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(getEndPoint(t))
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(clientBuilder.build())
                    .build();
            service = retrofit.create(t);
            mServiceMap.put(t.getName(), service);
        }
        return service;
    }

    /**
     *  获取EndPoint URL
     * @param t Service类型
     * @param <T>
     * @return
     */
    public static <T> String getEndPoint(Class<T> t) {
        String endPoint = "";
        if (t.getName().equals(FqbbHttpService.class.getName())) {
            endPoint = Constants.ENDPOINT_FQBB;
        }
        else if(t.getName().equals(TaoBaoSearchHttpService.class.getName()))
        {
            endPoint = Constants.ENDPOINT_TAOBAO_SEARCH;
        }
        else if(t.getName().equals(AlimamaHttpService.class.getName()))
        {
            endPoint = Constants.ENDPOINT_ALIMAMA_SEARCH;
        }
        else if(t.getName().equals(JifenBaoHttpService.class.getName()))
        {
            endPoint = Constants.ENDPOINT_JIFENBAO_SEARCH;
        }
        else if(t.getName().equals(TaoBaoSuggestHttpService.class.getName()))
        {
            endPoint = Constants.ENDPOINT_TAOBAO_SUGGESTS;
        }
        if ("".equals(endPoint)) {
            throw new IllegalArgumentException("Error: Can't get end point url. Please configure at the method " + ServiceManager.class.getSimpleName() + ".getEndPoint(T t)");
        }
        return endPoint;
    }

    /**
     * OkHttpClient的拦截器
     */
    static class ServiceInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response;
        }
    }
}