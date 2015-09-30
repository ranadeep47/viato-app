package in.viato.app.model;

/**
 * Created by saiteja on 21/09/15.
 */
public class Address {
    private String mAddress;
    private String mLocality;
    private String mLabel;

    public Address(String address, String locality, String label) {
        this.mAddress = address;
        this.mLocality = locality;
        this.mLabel = label;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getLocality() {
        return mLocality;
    }

    public String getLabel() {
        return mLabel;
    }
}

