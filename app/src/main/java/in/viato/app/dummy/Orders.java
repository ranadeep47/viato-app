package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.model.OrderInShort;

/**
 * Created by saiteja on 11/10/15.
 */
public class Orders {
    private List<OrderInShort> mOrdersList;

    public Orders() {
        mOrdersList = new ArrayList<OrderInShort>();

        for (int i = 0; i < 3; i++) {
            OrderInShort order = new OrderInShort();
            order.updatedOn.set("1-Aug-15");
            order.orderId.set(String.valueOf(i));
            order.orderStatus.set("Completed");
            order.orderValue.set(i * 5);
            mOrdersList.add(order);

            for (int j = 0; j < 2; j++) {
                order.covers.add("http://i.imgur.com/DvpvklR.png");
            }
        }
    }

    public List<OrderInShort> get() {
        return mOrdersList;
    }
}
