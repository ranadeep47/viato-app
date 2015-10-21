package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.ItemPricing;

/**
 * Created by saiteja on 15/09/15.
 */
public class Books {
    private List<BookItem> mBookList;

    public List<BookItem> get() {
        return mBookList;
    }

    public Books() {
        this.mBookList = new ArrayList<>();

        BookItem aBook = new BookItem();
        aBook.setTitle("The Busy Coder's Guide to Android Development");
        aBook.setAuthors(new String[]{"Paulo Coelho", "Amish Tripathi"});
        aBook.setCover("http://ecx.images-amazon.com/images/I/41MeC94AxIL._SL160_PIsitb-sticker-arrow-dp,TopRight,12,-18_SH30_OU31_SL150_.jpg");
        ItemPricing pricing1 = new ItemPricing();
        pricing1.setRent(98);
        aBook.setPricing(pricing1);
        mBookList.add(aBook);

        BookItem bBookItem = new BookItem();
        bBookItem.setTitle("The Busy Coder's Guide to Android Development");
        bBookItem.setAuthors(new String[]{"Paulo Coelho", "Amish Tripathi"});
        bBookItem.setCover("http://ecx.images-amazon.com/images/I/41MeC94AxIL._SL160_PIsitb-sticker-arrow-dp,TopRight,12,-18_SH30_OU31_SL150_.jpg");
        ItemPricing pricing2 = new ItemPricing();
        pricing1.setRent(98);
        bBookItem.setPricing(pricing2);
        mBookList.add(bBookItem);

        BookItem cBookItem = new BookItem();
        cBookItem.setTitle("The Busy Coder's Guide to Android Development");
        cBookItem.setAuthors(new String[]{"Paulo Coelho", "Amish Tripathi"});
        cBookItem.setCover("http://ecx.images-amazon.com/images/I/41MeC94AxIL._SL160_PIsitb-sticker-arrow-dp,TopRight,12,-18_SH30_OU31_SL150_.jpg");
        ItemPricing pricing3 = new ItemPricing();
        pricing3.setRent(98);
        cBookItem.setPricing(pricing3);
        mBookList.add(cBookItem);
    }
}
