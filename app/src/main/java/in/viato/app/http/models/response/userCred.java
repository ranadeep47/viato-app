package in.viato.app.http.models.response;

/**
 * Created by saiteja on 30/10/15.
 */
public class userCred {
    private String access_token;
    private String user_id;

    public userCred(String access_token, String user_id) {
        this.access_token = access_token;
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
