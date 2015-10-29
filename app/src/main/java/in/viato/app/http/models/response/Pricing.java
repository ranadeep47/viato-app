package in.viato.app.http.models.response;

import java.util.List;

/**
 * Created by saiteja on 17/10/15.
 */
public class Pricing {
    private List<ItemPricing> rental;
    private Owning owning;

    public Pricing(List<ItemPricing> rental, Owning owning) {
        this.rental = rental;
        this.owning = owning;
    }

    public List<ItemPricing> getRental() {
        return rental;
    }

    public void setRental(List<ItemPricing> rental) {
        this.rental = rental;
    }

    public Owning getOwning() {
        return owning;
    }

    public void setOwning(Owning owning) {
        this.owning = owning;
    }
}
