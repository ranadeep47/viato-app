package in.viato.app.http.models.response;

/**
 * Created by ranadeep on 16/10/15.
 */
public class CategoryImage {
    private String cover;
    private String square;

    public CategoryImage() {
        super();
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getSquare() {
        return square;
    }

    public void setSquare(String square) {
        this.square = square;
    }
}
