package in.viato.app.http.models.response;

/**
 * Created by saiteja on 31/10/15.
 */
public class Locality {
    String name;
    String place_id;

    public Locality(String name, String place_id) {
        this.name = name;
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
}
