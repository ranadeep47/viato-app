package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import java.util.List;

import in.viato.app.BR;

/**
 * Created by saiteja on 17/10/15.
 */
public class Pricing extends BaseObservable {
    private List<ItemPricing> rental;
    private Owning owning;

    @Bindable
    public List<ItemPricing> getRental() {
        return rental;
    }

    public void setRental(List<ItemPricing> rental) {
        this.rental = rental;
        notifyPropertyChanged(BR.rental);
    }

    @Bindable
    public Owning getOwning() {
        return owning;
    }

    public void setOwning(Owning owning) {
        this.owning = owning;
        notifyPropertyChanged(BR.owning);
    }
}
