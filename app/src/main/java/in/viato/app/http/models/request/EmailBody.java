package in.viato.app.http.models.request;

import java.util.List;

/**
 * Created by saiteja on 16/10/15.
 */
public class EmailBody {
    private String email;
    private String token;
    private List<Account> accounts;
    private String app_token;

    public EmailBody(String email, String token, List<Account> accounts, String app_token) {
        this.email = email;
        this.token = token;
        this.accounts = accounts;
        this.app_token = app_token;
    }
}
