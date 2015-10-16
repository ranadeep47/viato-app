package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import in.viato.app.BR;

/**
 * Created by ranadeep on 16/10/15.
 */
public class CategoryImage extends BaseObservable{
    private String cover;
    private String square;

    public CategoryImage() {
        super();
    }

    @Bindable
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
        notifyPropertyChanged(BR.cover);
    }

    @Bindable
    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
        notifyPropertyChanged(BR.square);
    }
}
