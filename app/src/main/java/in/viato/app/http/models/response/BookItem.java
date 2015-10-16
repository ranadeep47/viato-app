package in.viato.app.http.models.response;

/**
 * Created by ranadeep on 16/10/15.
 */
public class BookItem {
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
}
