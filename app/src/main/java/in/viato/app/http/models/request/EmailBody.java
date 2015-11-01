package in.viato.app.http.models.request;

import java.util.List;

/**
 * Created by saiteja on 16/10/15.
 */
public class EmailBody {
    private String email;
    private String token;
    private List<Account> accounts;

    public EmailBody(String email, String token, List<Account> accounts) {
        this.email = email;
        this.token = token;
        this.accounts = accounts;
    }
}
