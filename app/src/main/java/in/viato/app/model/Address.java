package in.viato.app.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;

/**
 * Created by saiteja on 21/09/15.
 */
public class Address extends BaseObservable {
    public final ObservableField<String> mAddress = new ObservableField<String>();
    public final ObservableField<String> mLocality = new ObservableField<String>();
    public final ObservableField<String> mLabel = new ObservableField<String>();
}

