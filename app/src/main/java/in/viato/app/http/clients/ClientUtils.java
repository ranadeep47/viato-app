package in.viato.app.http.clients;

import android.app.Application;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by ranadeep on 13/09/15.
 */
public class ClientUtils {

    public static OkHttpClient createOkHttpClient(Application app) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);
        client.setRetryOnConnectionFailure(true);

        int DISK_CACHE_SIZE = 1024*1024*10;
        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);

        return client;
    }
}
