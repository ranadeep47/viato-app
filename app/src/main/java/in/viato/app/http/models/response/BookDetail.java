package in.viato.app.http.models.response;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ranadeep on 17/10/15.
 */
public class BookDetail {
    private String _id;
    private String cover;
    private String title;
    private List<String> authors;
    private float rating;
    private Pricing pricing;
    private Boolean isInWishList;
    private String description;
    private String language;
    private Boolean available;
    private List<String> thumbs;
    private List<String> images;
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public BookDetail() {
        authors = new ArrayList<String>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        if(TextUtils.join(", ", authors) == null){
            return "";
        }
        return TextUtils.join(",", authors);
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<String> getThumbs() {
        return thumbs;
    }

    public void setThumbs(List<String> thumbs) {
        this.thumbs = thumbs;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Pricing getPricing() {
        return pricing;
    }

    public void setPricing(Pricing pricing) {
        this.pricing = pricing;
    }

    public Boolean getIsInWishList() {
        if(isInWishList == null){
            return false;
        }
        return isInWishList;
    }

    public void setIsInWishList(Boolean isInWishList) {
        this.isInWishList = isInWishList;
    }

    @Override
    public String toString() {
        return ("id: " + _id
                + ", title " + title
                + ", cover " + cover);
    }
}
