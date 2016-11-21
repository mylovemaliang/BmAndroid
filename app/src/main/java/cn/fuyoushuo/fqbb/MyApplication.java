package cn.fuyoushuo.fqbb;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.util.IWxCallback;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import cn.fuyoushuo.fqbb.commonlib.utils.Constants;
import cn.fuyoushuo.fqbb.commonlib.utils.LoginInfoStore;
import cn.fuyoushuo.fqbb.view.crash.CrashHandler;

/**
 * Created by QA on 2016/6/27.
 */
public class MyApplication extends Application{

    private static Context context;

    private static DisplayMetrics displayMetrics;

    private RefWatcher mRefWatcher;

    private String channelValue = "";

    private String feedbackAppkey = "";

    private Stack<Activity> allActivitys = new Stack<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            feedbackAppkey = String.valueOf(appInfo.metaData.getInt("feedback_appkey"));
            channelValue = appInfo.metaData.getString("UMENG_CHANNEL");
        }catch (Exception e){

        }
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context).setMemoryTrimmableRegistry(new MemoryTrimmableRegistry() {
            @Override
            public void registerMemoryTrimmable(MemoryTrimmable trimmable) {
                trimmable.trim(MemoryTrimType.OnAppBackgrounded);
            }

            @Override
            public void unregisterMemoryTrimmable(MemoryTrimmable trimmable) {

            }
        }).build();
        Fresco.initialize(context,config);
        //初始化异常拦截器
        CrashHandler.getInstance().init(this);
        //用户登录信息管理
        LoginInfoStore.getIntance().init(this);
        //禁用默认的统计机制
        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        displayMetrics = context.getResources().getDisplayMetrics();
        mRefWatcher = Constants.DEBUG ?  LeakCanary.install(this) : RefWatcher.DISABLED;
        initFeedBack(feedbackAppkey);

    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context
                .getApplicationContext();
        return application.mRefWatcher;
    }

    //获取channel值
    public static String getChannelValue() {
        MyApplication application = (MyApplication) context
                .getApplicationContext();
        return application.channelValue;
    }

    public static MyApplication getMyapplication(){
        MyApplication application = (MyApplication) context
                .getApplicationContext();
        return application;
    }

    public static Context getContext(){
        return context;
    }

    public static DisplayMetrics getDisplayMetrics(){
        return displayMetrics;
    }

    /**
     * 初始化阿里百川 用户回馈
     * @param appKey 阿里百川申请 的appkey
     */
    private void initFeedBack(String appKey){
        //阿里百川用户反馈
        FeedbackAPI.initAnnoy((Application) context,appKey);
        Map<String,String> feedbackSetMap = new HashMap<String,String>();
        feedbackSetMap.put("pageTitle","意见反馈");
        feedbackSetMap.put("themeColor","#de323a");
        FeedbackAPI.setUICustomInfo(feedbackSetMap);
        FeedbackAPI.setCustomContact("",true);
        FeedbackAPI.getFeedbackUnreadCount(context, null, new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i) {

            }
        });
    }

    private String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    //----------------------------------------activity 管理 --------------------------------------------

   public Activity getTopActivity(){
       return allActivitys.lastElement();
   }

    // activity管理：添加activity到列表
    public void addActivity(Activity activity) {
        allActivitys.add(activity);
    }

    // activity管理：从列表中移除activity
    public void removeActivity(Activity activity) {
        if(activity != null){
            allActivitys.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    // 移除栈顶Activity
    public void removeTopActivity(){
        Activity activity = allActivitys.lastElement();
        removeActivity(activity);
    }

    // activity管理：结束所有activity
    public void finishAllActivity() {
        for (Activity activity : allActivitys) {
            if (null != activity) {
                activity.finish();
            }
        }
        allActivitys.clear();
    }

    // 结束线程,一般与finishAllActivity()一起使用
    // 例如: finishAllActivity;finishProgram();
    public void finishProgram() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
