package cn.fuyoushuo.fqbb.commonlib.utils;

import java.io.File;

/**
 * @Project CommonProject
 * @Packate com.micky.commonlib.utils
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Date 2015-12-30 17:43
 * @Version 1.0
 */
public class Constants {

    //网络相关
    public static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;

    public static final String ENDPOINT_IP = "http://ip.taobao.com";
    public static final String ENDPOINT_WEATHER = " http://api.map.baidu.com";
    public static final String BAIDU_AK = "MPDgj92wUYvRmyaUdQs1XwCf";

    public static final String ENDPOINT_FQBB = "http://www.fanqianbb.com";

    public static final String ENDPOINT_TAOBAO_SEARCH="http://s.m.taobao.com";

    public static final String ENDPOINT_ALIMAMA_SEARCH="http://pub.alimama.com";

    public static final String ENDPOINT_JIFENBAO_SEARCH="http://ok.etao.com";

    public static final String ENDPOINT_TAOBAO_SUGGESTS="https://suggest.taobao.com";

    public static final String ENDPOINT_FQBB_LOCAL = "http://www.fanqianbb.com";

//    public static final String ENDPOINT_FQBB_LOCAL = "http://115.28.77.159:8085";



    public static final boolean DEBUG = false;

    //日志相关
    public static final String BASE_FILE_PATH = "fanqianbb";
    public static final String LOG_PATH = BASE_FILE_PATH + File.separator + "log";
    public static final String LOG_FILE = BASE_FILE_PATH + ".log";
}
