package in.viato.app.http.models.response;

import java.util.List;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksReadResponse {
    private List<BookItem> read;
    private List<BookItem> reading;

    public List<BookItem> getRead() {
        return read;
    }

    public void setRead(List<BookItem> read) {
        this.read = read;
    }

    public List<BookItem> getReading() {
        return reading;
    }

    public void setReading(List<BookItem> reading) {
        this.reading = reading;
    }
}
