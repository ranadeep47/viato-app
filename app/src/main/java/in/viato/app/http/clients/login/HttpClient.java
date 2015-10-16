package in.viato.app.http.clients.login;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.clients.ToStringConverterFactory;
import in.viato.app.http.models.request.EmailBody;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.request.OtpBody;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by saiteja on 15/10/15.
 */
public class HttpClient {

    //TODO: handle doOnError for all of them.
    private final HttpService mHttpService;

    public HttpClient() {
        OkHttpClient client = ClientUtils.createOkHttpClient(ViatoApplication.get());

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(HttpService.baseUrl)
                .client(client)
                .validateEagerly()
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mHttpService = retrofit.create(HttpService.class);
    }

    public Observable<String> login(String mobile, String deviceId, List<String> accounts){
        return mHttpService
                .login(new LoginBody(mobile, deviceId, accounts))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> verifyOTP(String otp, String mobile){
        return mHttpService
                .verifyOtp(new OtpBody(otp, mobile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<String> submitEmail(String email, String token) {
        return mHttpService
                .submitEmail(new EmailBody(email, token))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }
}
