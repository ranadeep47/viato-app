package in.viato.app.http.clients.viato;

import com.squareup.okhttp.OkHttpClient;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

/**
 * Created by ranadeep on 13/09/15.
 */
public class ViatoAPI {

    private final ViatoService mViatoService;

    public ViatoAPI() {
        OkHttpClient client = ClientUtils.createOkHttpClient(ViatoApplication.get());
        //Add interceptors
        client.interceptors().add(new AuthInterceptor());

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(ViatoService.baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mViatoService = retrofit.create(ViatoService.class);
    }

    //All the api methods go here
}
