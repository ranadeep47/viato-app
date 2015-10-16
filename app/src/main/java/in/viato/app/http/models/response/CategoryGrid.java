package in.viato.app.http.models.response;

import java.util.List;

/**
 * Created by ranadeep on 16/10/15.
 */
public class CategoryGrid {
    private String _id;
    private String title;
    private CategoryImage images;
    private List<BookItem> list;

    public CategoryGrid() {
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

    public List<BookItem> getList() {
        return list;
    }

    public void setList(List<BookItem> list) {
        this.list = list;
    }

    public CategoryImage getImages() {
        return images;
    }

    public void setImages(CategoryImage images) {
        this.images = images;
    }
}
