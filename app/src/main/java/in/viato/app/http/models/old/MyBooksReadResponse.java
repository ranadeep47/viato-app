package in.viato.app.http.models.old;

import java.util.List;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksReadResponse {
    private List<MyBook> read;
    private List<MyBook> reading;

    public List<MyBook> getRead() {
        return read;
    }

    public void setRead(List<MyBook> read) {
        this.read = read;
    }

    public List<MyBook> getReading() {
        return reading;
    }

    public void setReading(List<MyBook> reading) {
        this.reading = reading;
    }
}
