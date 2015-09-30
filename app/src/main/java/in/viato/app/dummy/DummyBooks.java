package in.viato.app.dummy;

import android.content.Context;
import android.databinding.ObservableField;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.model.Book;

/**
 * Created by saiteja on 15/09/15.
 */
public class DummyBooks {
    private static DummyBooks sDummyBooks;
    private Context sContext;
    private List<Book> sBooksList;

    public static DummyBooks get(Context context) {
        if(sDummyBooks == null) {
            sDummyBooks = new DummyBooks(context);
        }
        return sDummyBooks;
    }

    public DummyBooks(Context context) {
        this.sContext = context;
        this.sBooksList = new ArrayList<>();

        Book aBook = new Book();
        aBook.name.set("The Busy Coder's Guide to Android Development");
        aBook.author.set("Mark L. Murphy");
        aBook.rentPrice.set("Rs 20.00");
        sBooksList.add(aBook);

        Book bBook = new Book();
        bBook.name.set("Fifty Shades of Grey");
        bBook.author.set("E. L. James");
        bBook.rentPrice.set("Rs 40.00");
        sBooksList.add(bBook);

        Book cBook = new Book();
        cBook.name.set("Ikshvaku Ke Vanshaj");
        cBook.author.set("Amish Tripathi, Urmila Gupta");
        cBook.rentPrice.set("Rs 33.00");
        sBooksList.add(cBook);
    }

    public Book getBook(int pos) {
        return sBooksList.get(pos);
    }

    public List<Book> getBooks() {
        return sBooksList;
    }
}
