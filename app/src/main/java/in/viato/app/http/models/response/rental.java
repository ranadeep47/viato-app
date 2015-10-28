package in.viato.app.http.models.response;

import java.util.Date;

/**
 * Created by saiteja on 22/10/15.
 */
public class Rental {
    private String _id;
    private Date expires_at;
    private String status;
    private Payment extension_payment;
    private BookItem item;

    private Boolean is_picked;
    private Date pickup_requested_at;
    private Date pickup_done_at;

    private Boolean is_extended;
    private Date extended_at;

    private Boolean is_delivered;
    private Date delivered_at;
    private Date expected_delivery_at;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public BookItem getItem() {
        return item;
    }

    public void setItem(BookItem item) {
        this.item = item;
    }

    public Date getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(Date expires_at) {
        this.expires_at = expires_at;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIs_picked() {
        return is_picked;
    }

    public void setIs_picked(Boolean is_picked) {
        this.is_picked = is_picked;
    }

    public Date getPickup_done_at() {
        return pickup_done_at;
    }

    public void setPickup_done_at(Date pickup_done_at) {
        this.pickup_done_at = pickup_done_at;
    }

    public Date getPickup_requested_at() {
        return pickup_requested_at;
    }

    public void setPickup_requested_at(Date pickup_requested_at) {
        this.pickup_requested_at = pickup_requested_at;
    }

    public Boolean getIs_extended() {
        return is_extended;
    }

    public void setIs_extended(Boolean is_extended) {
        this.is_extended = is_extended;
    }

    public Date getExtended_at() {
        return extended_at;
    }

    public void setExtended_at(Date extended_at) {
        this.extended_at = extended_at;
    }

    public Payment getExtension_payment() {
        return extension_payment;
    }

    public void setExtension_payment(Payment extension_payment) {
        this.extension_payment = extension_payment;
    }

    public Boolean getIs_delivered() {
        return is_delivered;
    }

    public void setIs_delivered(Boolean is_delivered) {
        this.is_delivered = is_delivered;
    }

    public Date getDelivered_at() {
        return delivered_at;
    }

    public void setDelivered_at(Date delivered_at) {
        this.delivered_at = delivered_at;
    }

    public Date getExpected_delivery_at() {
        return expected_delivery_at;
    }

    public void setExpected_delivery_at(Date expected_delivery_at) {
        this.expected_delivery_at = expected_delivery_at;
    }
}
