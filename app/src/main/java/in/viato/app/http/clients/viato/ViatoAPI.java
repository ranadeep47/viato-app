package in.viato.app.http.clients.viato;

import com.squareup.okhttp.OkHttpClient;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import java.util.List;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.models.response.Book;
import in.viato.app.http.models.response.Category;
import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.MyBooksOwnResponse;
import in.viato.app.http.models.response.MyBooksReadResponse;
import in.viato.app.http.models.response.MyBooksWishlistResponse;
import in.viato.app.http.models.response.SearchResultItem;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.response.SimpleResponse;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ranadeep on 13/09/15.
 */
public class ViatoAPI {

    //TODO: handle doOnError for all of them.
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
    public Observable<SimpleResponse> login(String mobile){
        return mViatoService
                .login(new LoginBody(mobile))
                .observeOn(AndroidSchedulers.mainThread());
//              .subscribeOn(Schedulers.io()) Unncessary since retrofit already uses a thread executor pool

    }

    public Observable<List<Category>> getCategories(){
        return mViatoService
                .getCategories()
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Book>> getBooksByCategory(String category, int page){
        return mViatoService
                .getBooksByCategory(category, page)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<SearchResultItem>> search(String query){
        return mViatoService
                .search(query)
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<CoverQuote>> getQuotes(){
        return mViatoService.getQuotes().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyBooksOwnResponse> getOwned(){
        return mViatoService.getOwned().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyBooksReadResponse> getRead(){
        return mViatoService.getRead().observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MyBooksWishlistResponse> getWishlist(){
        return mViatoService.getWishlist().observeOn(AndroidSchedulers.mainThread());
    }
}
