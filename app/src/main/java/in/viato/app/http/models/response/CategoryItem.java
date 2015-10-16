package in.viato.app.http.models.response;

/**
 * Created by ranadeep on 16/10/15.
 */
public class CategoryItem {
    private String _id;
    private String title;
    private CategoryImage images;

    public CategoryItem() {
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

    public CategoryImage getImages() {
        return images;
    }

    public void setImages(CategoryImage images) {
        this.images = images;
    }
}
