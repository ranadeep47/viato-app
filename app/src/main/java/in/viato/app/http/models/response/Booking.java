package in.viato.app.http.models.response;

import java.util.Date;
import java.util.List;

import in.viato.app.http.models.Address;

/**
 * Created by saiteja on 22/10/15.
 */
public class Booking {
    private String _id;
    private String order_id;
    private String status;
    private Date booked_at;
    private Payment booking_payment;
    private Address delivery_address;
    private Address pickup_address;
    private List<Rental> rentals;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Address getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(Address delivery_address) {
        this.delivery_address = delivery_address;
    }

    public Address getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(Address pickup_address) {
        this.pickup_address = pickup_address;
    }

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public Payment getBooking_payment() {
        return booking_payment;
    }

    public void setBooking_payment(Payment booking_payment) {
        this.booking_payment = booking_payment;
    }

    public Date getBooked_at() {
        return booked_at;
    }

    public void setBooked_at(Date booked_at) {
        this.booked_at = booked_at;
    }
}
