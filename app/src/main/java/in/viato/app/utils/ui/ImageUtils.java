package in.viato.app.utils.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;

import in.viato.app.R;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ranadeep on 15/09/15.
 */
public class ImageUtils {

    public static Observable<Bitmap> getScaledBitmap(final Context context, final int drawable){
        Observable<Bitmap> observable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Display display;
                display = ((Activity)context).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                Bitmap bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        context.getResources(), drawable),size.x,size.y,true);

                subscriber.onNext(bmp);
            }
        });

        return observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread());

    }
}
