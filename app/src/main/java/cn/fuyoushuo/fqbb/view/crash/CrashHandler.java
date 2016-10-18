package cn.fuyoushuo.fqbb.view.crash;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.commonlib.utils.EventIdConstants;
import cn.fuyoushuo.fqbb.view.activity.SearchActivity;
import cn.fuyoushuo.fqbb.view.activity.WebviewActivity;

/**
 * @Description 全局Crash捕获处理
 * @Author Micky Liu
 * @Email sglazelhw@126.com
 * @Date 2015-04-03 下午 1:43
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler  {

//    public final Logger mLogger = Logger.getLogger(getClass());

    private static CrashHandler INSTANCE = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultUEH;
    public static final String TAG = "CatchExcep";
    private MyApplication mApplication;

    private CrashHandler() {
        mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    public void init(MyApplication application) {
        Thread.setDefaultUncaughtExceptionHandler(this);
        mApplication = application;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e("CrashHandler", ex.getMessage(), ex);
        if(!handleException(ex) && mDefaultUEH != null){
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultUEH.uncaughtException(thread, ex);
        }else{
            try{
                Thread.sleep(1000);
            }catch (InterruptedException e){
                Log.e(TAG, "error : ", e);
            }
            Activity topActivity = mApplication.getMyapplication().getTopActivity();
            Intent intent = new Intent(mApplication,topActivity.getClass());
            //初始化 待恢复的activity
            intent.putExtra("isCrash",true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(topActivity != null && topActivity instanceof WebviewActivity){
                intent.putExtra("loadUrl",((WebviewActivity) topActivity).getOriginLoadUrl());
            }else if(topActivity != null && topActivity instanceof SearchActivity){

            }
            PendingIntent restartIntent = PendingIntent.getActivity(mApplication,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
            //退出程序
            AlarmManager mgr = (AlarmManager)mApplication.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启响应的activity
            // 关闭当前应用
            //保存统计数据
            MobclickAgent.onEvent(mApplication, EventIdConstants.APP_CRASH_RESTART);
            MobclickAgent.onKillProcess(mApplication);
            mApplication.getMyapplication().finishAllActivity();
            mApplication.getMyapplication().finishProgram();
        }
    }


    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        //使用Toast来显示异常信息
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mApplication,"很抱歉,应用出现异常,即将重启!请耐心等待",
                        Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }.start();
        return true;
    }



}
