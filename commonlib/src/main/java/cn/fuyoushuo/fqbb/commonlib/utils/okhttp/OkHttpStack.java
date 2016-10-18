package cn.fuyoushuo.fqbb.commonlib.utils.okhttp;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;

import com.android.volley.toolbox.HurlStack;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

public class OkHttpStack extends HurlStack {
    private final OkUrlFactory okUrlFactory;

    public OkHttpStack(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setCookieHandler(new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL));
        client.setFollowRedirects(true);
        this.okUrlFactory = new OkUrlFactory(client);
    }

    public OkHttpStack(OkUrlFactory okUrlFactory) {
        if (okUrlFactory == null) {
            throw new NullPointerException("Client must not be null.");
        }
        this.okUrlFactory = okUrlFactory;
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return okUrlFactory.open(url);
    }
}