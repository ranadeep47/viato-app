package in.viato.app.http.models.request;

/**
 * Created by saiteja on 21/10/15.
 */
public class BookingBody {
    private String addressId;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public BookingBody(String addressId) {
        this.addressId = addressId;
    }
}
