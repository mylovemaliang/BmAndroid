package cn.fuyoushuo.fqbb.view.Layout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.fuyoushuo.fqbb.MyApplication;
import cn.fuyoushuo.fqbb.commonlib.utils.VolleyRqFactory;
import cn.fuyoushuo.fqbb.domain.entity.UpdateInfo;
import cn.fuyoushuo.fqbb.view.activity.BaseActivity;

public class AppUpdateView extends BaseActivity{

    private BaseActivity currentActivity;

    public AppUpdateView(BaseActivity currentActivity){
        this.currentActivity = currentActivity;
    }

    private String downloadUrl;

    private UpdateInfo info;
    private ProgressDialog pBar;
    private boolean autoCheckUpdate = true;

    public void getUpdateInfo(boolean autoCheck) {
        if(autoCheck && !isWifi(currentActivity)){
            return;
        }

        this.autoCheckUpdate = autoCheck;
        //RequestQueue volleyRq = Volley.newRequestQueue(currentActivity, new OkHttpStack(currentActivity));

        String channel = MyApplication.getChannelValue();

        RequestQueue volleyRq = VolleyRqFactory.getInstance().getQequestQueue(MyApplication.getContext());
        String url = "http://www.fanqianbb.com/mobile/update.htm?t=0&c="+channel;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            if(response!=null && !"".equals(response.trim())){
                                //response = new String(response.getBytes("iso-8859-1"), "UTF-8");
                                JSONObject jsonObj = JSONObject.parseObject(response);
                                JSONObject updateJsonObj = jsonObj.getJSONObject("r");
                                if(jsonObj.getIntValue("s")==1 && updateJsonObj!=null){
                                    info = new UpdateInfo();
                                    info.setVersion(updateJsonObj.getInteger("version"));
                                    info.setUrl(updateJsonObj.getString("downloadUrl"));
                                    info.setVersionName(updateJsonObj.getString("versionName"));
                                    info.setDescription(updateJsonObj.getString("desc"));
                                    handler1.sendEmptyMessage(0);
                                }
                            }
                        }catch(Exception e){

                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 出错了怎么办，显示通信失败信息
                    }
                });

        // 把这个请求加入请求队列
        volleyRq.add(stringRequest);
    }

    @SuppressLint("HandlerLeak")
    private Handler handler1 = new Handler() {
        public void handleMessage(Message msg) {//这里的场景，是在异步子线程获取到远程的app最新版本信息后，send一个Message过来处理
            // 如果有更新就提示
            if (isNeedUpdate()) {   //在下面的代码段
                Log.i("升级信息", "检测到新版本，需要升级！");

                showUpdateDialog(isWifi(currentActivity));  //下面的代码段
            }/*else{
                Log.i("升级信息", "已经是最新版本了，不需要升级！");
            }*/

            autoCheckUpdate = true;
        };
    };

    private void showUpdateDialog(boolean isWifi) {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentActivity);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle("检测到新版本("+info.getVersionName()+")");

        String sss = "\n";
        if(isWifi) {
            builder.setMessage(info.getDescription());  //设置info.getDescription()
        }else{
            builder.setMessage("当前并非Wifi网络，下载升级包可能会影响你的网络资费，请确认是否升级！\n\n" + info.getDescription());  //设置info.getDescription()
        }

        builder.setCancelable(false);
        builder.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //downFile(info.getUrl());     //在下面的代码段

                    downloadUrl = info.getUrl();
                    downFile();
                    //verifyStoragePermissions(currentActivity);
                } else {
                    Toast.makeText(currentActivity, "SD卡不可用，请插入SD卡",   Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        builder.create().show();
    }

    private boolean isNeedUpdate() {
        Integer v = info.getVersion();

        if (v > getCurrentAppVersion()) {
            return true;
        } else {
            if(!autoCheckUpdate)
                Toast.makeText(currentActivity, "当前已是最新版本("+info.getVersionName()+")", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    // 获取当前版本的版本号
    private int getCurrentAppVersion() {
        try {
            PackageManager packageManager = currentActivity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(currentActivity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void downFile() {
        pBar = new ProgressDialog(currentActivity);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();

        final String url = downloadUrl;

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder().url(url).build();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                InputStream is = null;
                FileOutputStream fileOutputStream = null;
                try {
                    is = response.body().byteStream();
                    int length = (int) response.body().contentLength();

                    pBar.setMax(length);                            //设置进度条的总长度
                    if (is != null) {
                        File file = new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "fqbnewest.apk");
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1024];   //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process);       //这里就是关键的实时更新进度了！
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (Exception e) {
                    Log.d("update app", "文件下载失败");
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fileOutputStream != null)
                            fileOutputStream.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
        });
    }

    //下载软件包的异步线程执行完下载任务，会调用这个方法往UI线程post这个要执行的逻辑（取消进度条显示，执行升级安装文件更新当前APP）
    void down() {
        handler1.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    //安装文件，一般固定写法
    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(MyApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "fqbnewest.apk")), "application/vnd.android.package-archive");
        currentActivity.startActivity(intent);
    }

    private static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }else{
            downFile();
        }
    }

}
