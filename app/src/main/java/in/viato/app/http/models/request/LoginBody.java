package in.viato.app.http.models.request;

/**
 * Created by ranadeep on 17/09/15.
 */
public class LoginBody {

    private String mobile;

    public LoginBody(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
