package in.viato.app.http.models.request;

/**
 * Created by saiteja on 28/10/15.
 */
public class RentalBody {
    String rentId;

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public RentalBody(String rentId) {
        this.rentId = rentId;
    }
}
