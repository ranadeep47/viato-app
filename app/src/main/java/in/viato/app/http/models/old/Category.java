package in.viato.app.http.models.old;

/**
 * Created by ranadeep on 19/09/15.
 */
public class Category {
    private String title;
    private String image;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "title='" + title + "\'" +
                ", image='" + image + "\'" +
                ", id='" + id + "\'" +
                "}";
    }
}
