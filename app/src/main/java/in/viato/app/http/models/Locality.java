package in.viato.app.http.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by saiteja on 18/10/15.
 */
public class Locality implements Parcelable {
    private String placeId ;
    private String name;

    public Locality(String placeId, String name) {
        this.placeId = placeId;
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.placeId);
        dest.writeString(this.name);
    }

    protected Locality(Parcel in) {
        this.placeId = in.readString();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Locality> CREATOR = new Parcelable.Creator<Locality>() {
        public Locality createFromParcel(Parcel source) {
            return new Locality(source);
        }

        public Locality[] newArray(int size) {
            return new Locality[size];
        }
    };
}