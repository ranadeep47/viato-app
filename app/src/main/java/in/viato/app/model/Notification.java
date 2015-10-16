package in.viato.app.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableField;

/**
 * Created by saiteja on 05/10/15.
 */
public class Notification extends BaseObservable {
    public final ObservableField<String> heading = new ObservableField<String>();
    public final ObservableField<String> body = new ObservableField<String>();
    public final ObservableField<String> date = new ObservableField<String>();
}
