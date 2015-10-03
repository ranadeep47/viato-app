package in.viato.app.model;

import java.util.ArrayList;

/**
 * Created by saiteja on 02/10/15.
 */
public class OrderInShort {
    private String updatedOn;
    private String orderId;
    private String orderStatus;
    private String orderValue;
    private ArrayList<String> covers;

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(String orderValue) {
        this.orderValue = orderValue;
    }

    public ArrayList<String> getCovers() {
        return covers;
    }

    public void setCovers(ArrayList<String> covers) {
        this.covers = covers;
    }
}
