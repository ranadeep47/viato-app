package in.viato.app.http.models.response;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import in.viato.app.BR;

/**
 * Created by saiteja on 17/10/15.
 */
public class Owning extends BaseObservable {
    private String mrp;

    @Bindable
    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
        notifyPropertyChanged(BR.mrp);
    }
}
