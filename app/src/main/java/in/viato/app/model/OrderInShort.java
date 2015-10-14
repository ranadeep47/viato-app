package in.viato.app.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

/**
 * Created by saiteja on 02/10/15.
 */
public class OrderInShort extends BaseObservable {
    public final ObservableField<String> updatedOn = new ObservableField<>();
    public final ObservableField<String> orderId = new ObservableField<>();
    public final ObservableField<String> orderStatus = new ObservableField<>();
    public final ObservableList<String> covers = new ObservableArrayList<>();
    public final ObservableInt orderValue = new ObservableInt();
}
