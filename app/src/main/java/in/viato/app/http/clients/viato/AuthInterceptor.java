package in.viato.app.http.clients.viato;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import in.viato.app.utils.AppConstants.UserInfo;


/**
 * Created by ranadeep on 13/09/15.
 */
public class AuthInterceptor implements Interceptor {
    public AuthInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header("Authorization", "Bearer " + UserInfo.INSTANCE.getAccessToken());
        builder.header("X-APP-VERSION", String.valueOf(UserInfo.INSTANCE.getAppVersion()));
        builder.header("X-DEVICE-ID", UserInfo.INSTANCE.getDeviceId());
        builder.header("X-APP-TOKEN", UserInfo.INSTANCE.getAppToken());
        return chain.proceed(builder.build());
    }

}
