package in.viato.app.http.models.response;

/**
 * Created by ranadeep on 17/09/15.
 */
public class SimpleResponse {
    private String message;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
