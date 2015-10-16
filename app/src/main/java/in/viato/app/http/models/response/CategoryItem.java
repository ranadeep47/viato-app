package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import in.viato.app.BR;

/**
 * Created by ranadeep on 16/10/15.
 */
public class CategoryItem extends BaseObservable {
    private String _id;
    private String title;
    private CategoryImage images;

    public CategoryItem() {
        super();
    }

    @Bindable
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
        notifyPropertyChanged(BR._id);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public CategoryImage getImages() {
        return images;
    }

    public void setImages(CategoryImage images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }
}
