package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.BR;

/**
 * Created by ranadeep on 17/10/15.
 */
public class BookDetail extends BaseObservable {
    private String _id;
    private String title;
    private List<String> authors;
    private String cover;
    private String language;
    private String description;
    private float rating;
    private Boolean available;
    private List<String> thumbs;
    private List<String> images;
    private Pricing pricing;
    private Boolean isInWishList;
    private String source;


    @Bindable
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
        notifyPropertyChanged(BR.source);
    }

    public BookDetail() {
        authors = new ArrayList<String>();
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
    public String getAuthors() {
        if(TextUtils.join(",", authors) == null){
            return "";
        }
        return TextUtils.join(",", authors);
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
        notifyPropertyChanged(BR.authors);
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
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        notifyPropertyChanged(BR.language);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
        notifyPropertyChanged(BR.rating);
    }

    @Bindable
    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
        notifyPropertyChanged(BR.available);
    }

    @Bindable
    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
        notifyPropertyChanged(BR.thumbs);
    }

    @Bindable
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable
    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
        notifyPropertyChanged(BR.pricing);
    }

    @Bindable
    public Boolean getIsInWishList() {
        return isInWishList;
    }

    public void setIsInWishList(Boolean isInWishList) {
        this.isInWishList = isInWishList;
        notifyPropertyChanged(BR.isInWishList);
    }

    @Override
    public String toString() {
        Logger.d("id: " + _id
        + "title " + title
        + "cover " + cover);
        return ("id: " + _id
                + "title " + title
                + "cover " + cover);
    }
}
