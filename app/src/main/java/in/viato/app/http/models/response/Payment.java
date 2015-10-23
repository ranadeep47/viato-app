package in.viato.app.http.models.response;

import java.util.Date;

/**
 * Created by saiteja on 22/10/15.
 */
public class Payment {
    private String mode;
    private float total_payable;
    private float coupon_discount;
    private String coupon_applied;
    private float discount;
    private float other_charges;
    private float tax;
    private float total;
    private Boolean is_paid;
    private Date paid_at;
    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Boolean getIs_paid() {
        return is_paid;
    }

    public void setIs_paid(Boolean is_paid) {
        this.is_paid = is_paid;
    }

    public float getTotal_payable() {
        return total_payable;
    }

    public void setTotal_payable(float total_payable) {
        this.total_payable = total_payable;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public float getTax() {
        return tax;
    }

    public void setTax(float tax) {
        this.tax = tax;
    }

    public float getOther_charges() {
        return other_charges;
    }

    public void setOther_charges(float other_charges) {
        this.other_charges = other_charges;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public float getCoupon_discount() {
        return coupon_discount;
    }

    public void setCoupon_discount(float coupon_discount) {
        this.coupon_discount = coupon_discount;
    }

    public Date getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(Date paid_at) {
        this.paid_at = paid_at;
    }

    public String getCoupon_applied() {
        return coupon_applied;
    }

    public void setCoupon_applied(String coupon_applied) {
        this.coupon_applied = coupon_applied;
    }
}
