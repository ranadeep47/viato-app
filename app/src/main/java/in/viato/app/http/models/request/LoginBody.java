package in.viato.app.http.models.request;

import java.util.List;

/**
 * Created by ranadeep on 17/09/15.
 */
public class LoginBody {
    private String mobile;
    private String device_id;

    public LoginBody(String mobile, String deviceId) {
        this.mobile = mobile;
        this.device_id = deviceId;
    }
}
