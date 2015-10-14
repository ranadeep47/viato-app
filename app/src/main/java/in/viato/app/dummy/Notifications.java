package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.model.Notification;

/**
 * Created by saiteja on 09/10/15.
 */
public class Notifications {
    private List<Notification> mNotificationList;

    public List<Notification> get() {
        mNotificationList = new ArrayList<>();

        Notification aNotification = new Notification();
        aNotification.heading.set("The Busy Coder's Guide to Android Development");
        aNotification.body.set("Mark L. Murphy");
        aNotification.date.set("Rs 20.00");
        mNotificationList.add(aNotification);

        Notification bNotification = new Notification();
        bNotification.heading.set("Fifty Shades of Grey");
        bNotification.body.set("E. L. James");
        bNotification.date.set("Rs 40.00");
        mNotificationList.add(bNotification);

        Notification cNotification = new Notification();
        cNotification.heading.set("Ikshvaku Ke Vanshaj");
        cNotification.body.set("Amish Tripathi, Urmila Gupta");
        cNotification.date.set("Rs 33.00");
        mNotificationList.add(cNotification);

        return mNotificationList;
    }
}
