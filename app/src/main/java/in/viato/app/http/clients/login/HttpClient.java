package in.viato.app.http.clients.login;

import com.squareup.okhttp.OkHttpClient;

import java.util.List;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.clients.ToStringConverterFactory;
import in.viato.app.http.models.request.Account;
import in.viato.app.http.models.request.EmailBody;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.request.OtpBody;
import retrofit.MoshiConverterFactory;
import retrofit.Response;
import retrofit.Result;
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

    public Observable<Response<String>> login(String mobile, String deviceId){
        return mHttpService
                .login(new LoginBody(mobile, deviceId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> verifyOTP(String otp, String mobile){
        return mHttpService
                .verifyOtp(new OtpBody(otp, mobile))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<String> finishLogin(String email, String token, List<Account> accounts) {
        return mHttpService
                .finishLogin(new EmailBody(email, token, accounts))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }
}
