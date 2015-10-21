package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.R;
import in.viato.app.model.Notification;

/**
 * Created by saiteja on 09/10/15.
 */
public class Notifications {
    private List<Notification> mNotificationList;

    public List<Notification> get() {
        mNotificationList = new ArrayList<>();

        Notification aNotification = new Notification();
        aNotification.setTitle("Headin 1");
        aNotification.setBody("Lorem ipsum <strike> crossed </strike> dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        aNotification.setDate("12-Sep-15");
        mNotificationList.add(aNotification);

        Notification bNotification = new Notification();
        bNotification.setTitle("Heading 2");
        bNotification.setBody("Lorem ipsum <strike> crossed </strike> dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        bNotification.setDate("10-Sep-15");
        mNotificationList.add(bNotification);

        Notification cNotification = new Notification();
        cNotification.setTitle("Heading 3");
        cNotification.setBody("Lorem ipsum <strike> crossed </strike> dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        cNotification.setDate("1-Sep-15");
        mNotificationList.add(cNotification);

        return mNotificationList;
    }
}
