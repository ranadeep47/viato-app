package in.viato.app.utils.Binding;

import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.viato.app.R;
import in.viato.app.ui.adapters.RelatedBooksRVAdapter;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.ui.widgets.MyVerticalLlm;

/**
 * Created by saiteja on 03/10/15.
 */
public class CustomBindingAdapter extends BaseObservable {
    @BindingAdapter({"android:src", "android:error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
        Picasso.with(view.getContext())
                .load(url)
                .error(error)
                .into(view);
    }

    @BindingAdapter({"android:items", "android:setAdapter"})
    public static void setAdapter(RecyclerView view, List<String> list, String adapter) {
        switch (adapter) {
            case "RelatedBooksRVAdapter":
                RelatedBooksRVAdapter rvAdapter = new RelatedBooksRVAdapter(R.layout.p_book_thumbnail_small, list, false);
                view.setLayoutManager(new MyHorizantalLlm(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
                view.setAdapter(rvAdapter);
                view.setHasFixedSize(true);
                break;
        }

    }
}
