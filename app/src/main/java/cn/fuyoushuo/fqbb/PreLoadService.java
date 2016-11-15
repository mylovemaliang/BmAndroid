package cn.fuyoushuo.fqbb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class PreLoadService extends Service {

    public PreLoadService() {
        Log.d("预加载服务开启","1111111111111111");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
