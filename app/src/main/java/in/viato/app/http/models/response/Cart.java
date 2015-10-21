package in.viato.app.http.models.response;

import java.util.List;

import in.viato.app.http.models.Address;

/**
 * Created by saiteja on 20/10/15.
 */
public class Cart {
    private List<BookItem> cart;
    private List<Address> addresses;

    public List<BookItem> getCart() {
        return cart;
    }

    public void setCart(List<BookItem> cart) {
        this.cart = cart;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}
