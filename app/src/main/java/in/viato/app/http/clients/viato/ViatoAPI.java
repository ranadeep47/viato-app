package in.viato.app.http.clients.viato;

import com.squareup.okhttp.OkHttpClient;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.http.models.response.CategoryItem;
import retrofit.MoshiConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import java.util.List;

import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.MyBooksReadResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
                .validateEagerly()
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        mViatoService = retrofit.create(ViatoService.class);
    }


    public Observable<List<CategoryItem>> getCategories(){
        return mViatoService
                .getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<CategoryGrid> getBooksByCategory(String categoryId, int page){
        return mViatoService
                .getBooksByCategory(categoryId, page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<List<BookItem>> search(String query){
        return mViatoService
                .search(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<List<CoverQuote>> getQuotes(){
        return mViatoService
                .getQuotes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<MyBooksReadResponse> getRead(){
        return mViatoService
                .getRead()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<List<BookItem>> getWishlist(){
        return mViatoService
                .getWishlist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
