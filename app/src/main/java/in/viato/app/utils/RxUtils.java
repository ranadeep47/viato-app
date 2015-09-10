package in.viato.app.utils;

/**
 * Created by ranadeep on 11/09/15.
 */
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class RxUtils {

    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }
}
