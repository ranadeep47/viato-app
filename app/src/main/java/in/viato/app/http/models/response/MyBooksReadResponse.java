package in.viato.app.http.models.response;

import java.util.List;

import in.viato.app.http.models.old.MyBook;

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
