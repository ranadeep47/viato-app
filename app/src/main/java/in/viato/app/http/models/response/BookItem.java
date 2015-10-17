package in.viato.app.http.models.response;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ranadeep on 16/10/15.
 */

public class BookItem implements Parcelable {
    private String _id;
    private String title;
    private String cover;
    private String catalogueId;
    private String extraId;
    private String extraKey;
    private ItemPricing pricing;
    private String[] thumbs;
    private String[] authors;

    public BookItem() {
        super();
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

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(String catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getExtraId() {
        return extraId;
    }

    public void setExtraId(String extraId) {
        this.extraId = extraId;
    }

    public String getExtraKey() {
        return extraKey;
    }

    public void setExtraKey(String extraKey) {
        this.extraKey = extraKey;
    }

    public ItemPricing getPricing() {
        return pricing;
    }

    public void setPricing(ItemPricing pricing) {
        this.pricing = pricing;
    }

    public String[] getThumbs() {
        return thumbs;
    }

    public void setThumbs(String[] thumbs) {
        this.thumbs = thumbs;
    }

    public String[] getAuthors() {
        return authors;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.title);
        dest.writeString(this.cover);
        dest.writeString(this.catalogueId);
        dest.writeString(this.extraId);
        dest.writeString(this.extraKey);
        dest.writeParcelable(this.pricing, flags);
        dest.writeStringArray(this.thumbs);
        dest.writeStringArray(this.authors);
    }

    protected BookItem(Parcel in) {
        this._id = in.readString();
        this.title = in.readString();
        this.cover = in.readString();
        this.catalogueId = in.readString();
        this.extraId = in.readString();
        this.extraKey = in.readString();
        this.pricing = in.readParcelable(ItemPricing.class.getClassLoader());
        this.thumbs = in.createStringArray();
        this.authors = in.createStringArray();
    }

    public static final Parcelable.Creator<BookItem> CREATOR = new Parcelable.Creator<BookItem>() {
        public BookItem createFromParcel(Parcel source) {
            return new BookItem(source);
        }

        public BookItem[] newArray(int size) {
            return new BookItem[size];
        }
    };
}
