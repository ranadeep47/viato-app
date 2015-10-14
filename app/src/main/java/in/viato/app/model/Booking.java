package in.viato.app.model;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableLong;

import java.util.Date;

/**
 * Created by saiteja on 13/10/15.
 */
public class Booking {
    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> sub_title = new ObservableField<>();
    public final ObservableInt price = new ObservableInt();
    public final ObservableField<String> cover = new ObservableField<>();
    public final ObservableField<Date> order_date = new ObservableField<>();
    public final ObservableField<Date> return_date = new ObservableField<>();
    public final ObservableLong return_period = new ObservableLong();
    public final ObservableBoolean isExtendAvailable = new ObservableBoolean();
}
