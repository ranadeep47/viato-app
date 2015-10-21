package in.viato.app.dummy;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.http.models.Address;
import in.viato.app.http.models.Locality;

/**
 * Created by saiteja on 09/10/15.
 */
public class Addresses {
    private List<Address> mAddressList;

    public Addresses() {
        mAddressList = new ArrayList<>();

        Address aAddress = new Address();
        aAddress.setFlat("#108, B Wing");
        aAddress.setStreet("Building No. 32, Sahyadri CHS, MHADA");
        aAddress.setLocality(new Locality("23456789", "Chandivali, Mumbai"));
        mAddressList.add(aAddress);

        Address bAddress = new Address();
        bAddress.setFlat("#108, B Wing");
        bAddress.setStreet("Building No. 32, Sahyadri CHS, MHADA");
        aAddress.setLocality(new Locality("23456789", "Chandivali, Mumbai"));
        mAddressList.add(bAddress);

        Address cAddress = new Address();
        cAddress.setFlat("#108, B Wing");
        cAddress.setStreet("Building No. 32, Sahyadri CHS, MHADA");
        aAddress.setLocality(new Locality("23456789", "Chandivali, Mumbai"));
        mAddressList.add(cAddress);
    }

    public List<Address> get() {
        return mAddressList;
    }
}
