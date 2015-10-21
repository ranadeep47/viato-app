package in.viato.app.http.models.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ranadeep on 16/10/15.
 */
public class Rental implements Parcelable {
    private String _id;
    private float rent;
    private int period;

    public Rental() {
        super();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public float getRent() {
        return rent;
    }

    public void setRent(float rent) {
        this.rent = rent;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeFloat(this.rent);
        dest.writeInt(this.period);
    }

    protected Rental(Parcel in) {
        this._id = in.readString();
        this.rent = in.readFloat();
        this.period = in.readInt();
    }

    public static final Creator<Rental> CREATOR = new Creator<Rental>() {
        public Rental createFromParcel(Parcel source) {
            return new Rental(source);
        }

        public Rental[] newArray(int size) {
            return new Rental[size];
        }
    };
}
