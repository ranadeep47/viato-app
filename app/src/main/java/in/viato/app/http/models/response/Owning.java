package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import in.viato.app.BR;

/**
 * Created by saiteja on 17/10/15.
 */
public class Owning extends BaseObservable {
    private float mrp;

    @Bindable
    public int getMrp() {
        return (int)mrp;
    }

    public void setMrp(float mrp) {
        this.mrp = mrp;
        notifyPropertyChanged(BR.mrp);
    }
}
