package in.viato.app.http.clients.login;

import in.viato.app.http.models.request.EmailBody;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.request.OtpBody;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by saiteja on 15/10/15.
 */
public interface HttpService {
    String baseUrl = "http://viato.in";

    @POST("/login/")
    Observable<String> login(@Body LoginBody body);

    @POST("/login/otp/verify/")
    Observable<String> verifyOtp(@Body OtpBody otp);

    @POST("/login/complete/")
    Observable<String> submitEmail(@Body EmailBody otp);
}

