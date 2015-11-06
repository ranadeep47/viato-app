package in.viato.app.http.models;

/**
 * Created by saiteja on 06/11/15.
 */
public class ForceUpdate {
    private String message;
    private Boolean update;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }
}
