package in.viato.app.http.models.request;

import java.util.List;

/**
 * Created by ranadeep on 17/09/15.
 */
public class LoginBody {
    private String mobile;
    private String device_id;
    private List<String> accounts;

    public LoginBody(String mobile, String deviceId, List<String> accounts) {
        this.mobile = mobile;
        this.device_id = deviceId;
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "LoginBody {" +
                "mobile='" + mobile + '\'' +
                ", device_id='" + device_id +  '\'' +
                ", accounts=" + accounts.toString() +
                '}';
    }
}
