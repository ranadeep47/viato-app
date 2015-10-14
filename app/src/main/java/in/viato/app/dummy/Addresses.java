package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.model.Address;

/**
 * Created by saiteja on 09/10/15.
 */
public class Addresses {
    private List<Address> mAddressList;

    public Addresses() {
        mAddressList = new ArrayList<>();

        Address aAddress = new Address();
        aAddress.mAddress.set("The Busy Coder's Guide to Android Development");
        aAddress.mLabel.set("Mark L. Murphy");
        aAddress.mLocality.set("Rs 20.00");
        mAddressList.add(aAddress);

        Address bAddress = new Address();
        bAddress.mAddress.set("Fifty Shades of Grey");
        bAddress.mLabel.set("E. L. James");
        bAddress.mLocality.set("Rs 40.00");
        mAddressList.add(bAddress);

        Address cAddress = new Address();
        cAddress.mAddress.set("Ikshvaku Ke Vanshaj");
        cAddress.mLabel.set("Amish Tripathi, Urmila Gupta");
        cAddress.mLocality.set("Rs 33.00");
        mAddressList.add(cAddress);
    }

    public List<Address> get() {
        return mAddressList;
    }
}
