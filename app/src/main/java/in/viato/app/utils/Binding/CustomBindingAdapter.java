package in.viato.app.utils.Binding;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by saiteja on 03/10/15.
 */
public class CustomBindingAdapter extends BaseObservable {
    @BindingAdapter({"android:src", "android:error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
        Picasso.with(view.getContext()).load(url).error(error).into(view);
    }
}
