package in.viato.app.http.models.response;

import java.util.List;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksWishlistResponse {
    private List<MyBook> wishlist;

    public List<MyBook> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<MyBook> wishlist) {
        this.wishlist = wishlist;
    }
}
