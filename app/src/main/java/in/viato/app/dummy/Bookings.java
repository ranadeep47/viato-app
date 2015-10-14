package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.viato.app.model.Book;
import in.viato.app.model.Booking;

/**
 * Created by saiteja on 13/10/15.
 */
public class Bookings {
    private List<Booking> mBookingList;

    public List<Booking> get() {
        return mBookingList;
    }

    public Bookings() {
        this.mBookingList = new ArrayList<>();

        Booking aBooking = new Booking();
        aBooking.title.set("The Busy Coder's Guide to Android Development");
        aBooking.sub_title.set("Mark L. Murphy");
        aBooking.price.set(20);
        aBooking.cover.set("https://c1.staticflickr.com/1/751/21665490700_12180f62d2_n.jpg");
        aBooking.return_date.set(new Date());
        aBooking.order_date.set(new Date());
        aBooking.return_period.set(20);
        aBooking.isExtendAvailable.set(false);
        mBookingList.add(aBooking);

        Booking bBooking = new Booking();
        bBooking.title.set("Fifty Shades of Grey");
        bBooking.sub_title.set("E. L. James");
        bBooking.price.set(40);
        bBooking.cover.set("http://c2.staticflickr.com/6/5705/21664857848_c8e6750b26_n.jpg");
        bBooking.return_date.set(new Date());
        bBooking.order_date.set(new Date());
        bBooking.return_period.set(20);
        bBooking.isExtendAvailable.set(false);
        mBookingList.add(bBooking);

        Booking cBooking = new Booking();
        cBooking.title.set("Ikshvaku Ke Vanshaj");
        cBooking.sub_title.set("Amish Tripathi, Urmila Gupta");
        cBooking.price.set(33);
        cBooking.cover.set("https://c1.staticflickr.com/1/592/21841433136_983544a079.jpg");
        cBooking.return_date.set(new Date());
        cBooking.order_date.set(new Date());
        cBooking.return_date.set(new Date());
        cBooking.order_date.set(new Date());
        cBooking.return_period.set(20);
        cBooking.isExtendAvailable.set(false);
        mBookingList.add(cBooking);
    }
}
