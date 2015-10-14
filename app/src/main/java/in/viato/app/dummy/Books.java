package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.model.Book;

/**
 * Created by saiteja on 15/09/15.
 */
public class Books {
    private List<Book> mBookList;

    public List<Book> get() {
        return mBookList;
    }

    public Books() {
        this.mBookList = new ArrayList<>();

        Book aBook = new Book();
        aBook.title.set("The Busy Coder's Guide to Android Development");
        aBook.author.set("Mark L. Murphy");
        aBook.rentPrice.set("Rs 20.00");
        aBook.cover.set("https://c1.staticflickr.com/1/751/21665490700_12180f62d2_n.jpg");
        mBookList.add(aBook);

        Book bBook = new Book();
        bBook.title.set("Fifty Shades of Grey");
        bBook.author.set("E. L. James");
        bBook.rentPrice.set("Rs 40.00");
        bBook.cover.set("http://c2.staticflickr.com/6/5705/21664857848_c8e6750b26_n.jpg");
        mBookList.add(bBook);

        Book cBook = new Book();
        cBook.title.set("Ikshvaku Ke Vanshaj");
        cBook.author.set("Amish Tripathi, Urmila Gupta");
        cBook.rentPrice.set("Rs 33.00");
        cBook.cover.set("https://c1.staticflickr.com/1/592/21841433136_983544a079.jpg");
        mBookList.add(cBook);
    }
}
