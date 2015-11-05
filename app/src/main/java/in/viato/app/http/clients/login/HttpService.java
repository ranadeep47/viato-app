package in.viato.app.http.clients.login;

import in.viato.app.http.models.request.EmailBody;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.request.OtpBody;
import in.viato.app.http.models.response.LoginResponse;
import retrofit.Response;
import retrofit.Result;
import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by saiteja on 15/10/15.
 */
public interface HttpService {
    String baseUrl = "https://viato.in/";

    @POST("login")
    Observable<Response<String>> login(@Body LoginBody body);

    @POST("login/otp/verify")
    Observable<Response<String>> verifyOtp(@Body OtpBody otp);

    @POST("login/complete")
    Observable<Response<LoginResponse>> finishLogin(@Body EmailBody otp);
}

