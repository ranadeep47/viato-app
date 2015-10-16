package in.viato.app.http.models.request;

/**
 * Created by saiteja on 15/10/15.
 */
public class OtpBody {
    String otp;
    String mobile;

    public OtpBody(String otp, String mobile) {
        this.otp = otp;
        this.mobile = mobile;
    }
}
