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

    private final String mAccessToken;
    private final int mAppVersion;
    private final String mDeviceId;

    public AuthInterceptor() {
        mAccessToken = UserInfo.INSTANCE.getAccessToken();
        mAppVersion = UserInfo.INSTANCE.getAppVersion();
        mDeviceId = UserInfo.INSTANCE.getDeviceId();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        builder.header("Authorization", "token " + mAccessToken);
        builder.header("X-APP-VERSION", String.valueOf(mAppVersion));
        builder.header("X-DEVICE-ID", mDeviceId);
        return chain.proceed(builder.build());
    }

}
