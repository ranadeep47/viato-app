package in.viato.app.http.models.response;

/**
 * Created by ranadeep on 16/10/15.
 */
public class ItemPricing {
    private String _id;
    private float rent;
    private int period;

    public ItemPricing() {
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
}
