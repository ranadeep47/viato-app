package in.viato.app.utils.ui;

import android.content.Context;

import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

/**
 * Created by saiteja on 31/10/15.
 */
public class ImageHandler {
    private static Picasso instance;

    public static Picasso getSharedInstance(Context context) {
        if(instance == null) {
            instance = new Picasso.Builder(context)
                    .executor(Executors.newSingleThreadExecutor())
                    .memoryCache(Cache.NONE).indicatorsEnabled(true).build();
            return instance;
        } else {
            return instance;
        }
    }
}
