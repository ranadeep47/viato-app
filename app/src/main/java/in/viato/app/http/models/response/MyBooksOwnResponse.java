package in.viato.app.http.models.response;

import java.util.List;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksOwnResponse {
    private List<MyBook> shared;
    private List<MyBook> not_shared;

    public List<MyBook> getShared() {
        return shared;
    }

    public void setShared(List<MyBook> shared) {
        this.shared = shared;
    }

    public List<MyBook> getNot_shared() {
        return not_shared;
    }

    public void setNot_shared(List<MyBook> not_shared) {
        this.not_shared = not_shared;
    }
}
