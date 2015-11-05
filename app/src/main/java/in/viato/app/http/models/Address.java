package in.viato.app.http.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saiteja on 21/09/15.
 */
public class Address implements Parcelable {
    private String _id;
    private String flat;
    private String street;
    private Locality locality;
    private String label;
    private Boolean is_default;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public String getLabel() {
        if (label == null) {
            return null;
        }
        return Character.toUpperCase(label.charAt(0)) + label.substring(1);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getIs_default() {
        return is_default;
    }

    public void setIs_default(Boolean is_default) {
        this.is_default = is_default;
    }

    public Address() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.flat);
        dest.writeString(this.street);
        dest.writeParcelable(this.locality, 0);
        dest.writeString(this.label);
        dest.writeValue(this.is_default);
    }

    protected Address(Parcel in) {
        this._id = in.readString();
        this.flat = in.readString();
        this.street = in.readString();
        this.locality = in.readParcelable(Locality.class.getClassLoader());
        this.label = in.readString();
        this.is_default = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        public Address createFromParcel(Parcel source) {
            return new Address(source);
        }

        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    @Override
    public String toString() {
        return "Address { " +
                " _id: " + getId() +
                ", flat: " + getFlat() +
                ", street: " + getStreet() +
                ", locality name: " + getLocality().getName() +
                ", locality id: " + getLocality().getPlaceId() +
                ", label: " + getLabel();
    }
}

