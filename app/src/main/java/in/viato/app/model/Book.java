package in.viato.app.model;

import android.databinding.BaseObservable;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;
import android.databinding.ObservableLong;

import java.util.List;
import java.util.UUID;

/**
 * Created by saiteja on 15/09/15.
 */
public class Book extends BaseObservable {
    public final ObservableField<String> title = new ObservableField<String>();
    public final ObservableField<String> author = new ObservableField<String>();
    public final ObservableField<String> cover = new ObservableField<String>();
    public final ObservableField<String> genre = new ObservableField<String>();
    public final ObservableField<String> description = new ObservableField<String>();
    public final ObservableField<String> publisher = new ObservableField<String>();
    public final ObservableField<String> language = new ObservableField<String>();
    public final ObservableField<String> isbn = new ObservableField<String>();

    public final ObservableInt price = new ObservableInt();
    public final ObservableInt numberOfCopies = new ObservableInt();
    public final ObservableInt rating = new ObservableInt();
    public final ObservableInt salePrice = new ObservableInt();
    public final ObservableField<String> rentPrice = new ObservableField<String>();
    public final ObservableInt availableDuration = new ObservableInt();
    public final ObservableInt numberOfTimesRented = new ObservableInt();

    public final ObservableList<String> reviews = new ObservableArrayList<String>();

    public final ObservableBoolean isFavourite = new ObservableBoolean();
    public final ObservableBoolean isRead = new ObservableBoolean();
    public final ObservableBoolean isOwned = new ObservableBoolean();
    public final ObservableBoolean isAvailable = new ObservableBoolean();

    public final ObservableField<UUID> _id = new ObservableField<UUID>();

}

//public String title;
//public String subtitle;
//public List<String> authors;
//public String publisher;
//public String description;
//public ImageLinks imageLinks;
