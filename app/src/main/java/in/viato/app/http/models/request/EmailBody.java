package in.viato.app.http.models.request;


/**
 * Created by saiteja on 16/10/15.
 */
public class EmailBody {
    private String email;
    private String token;

    public EmailBody(String email, String token) {
        this.email = email;
        this.token = token;
    }
}
