package in.viato.app.http.models.request;

/**
 * Created by saiteja on 20/10/15.
 */
public class CartItem {
    private String catalogueId;
    private String rentalId;

    public CartItem(String catalogueId, String rentalId) {
        this.catalogueId = catalogueId;
        this.rentalId = rentalId;
    }

    public String getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(String catalogueId) {
        this.catalogueId = catalogueId;
    }

    public String getRentalId() {
        return rentalId;
    }

    public void setRentalId(String rentalId) {
        this.rentalId = rentalId;
    }
}
