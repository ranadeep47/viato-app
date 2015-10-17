package in.viato.app.http.clients.viato;

import com.squareup.okhttp.OkHttpClient;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.clients.ToStringConverterFactory;
import in.viato.app.http.models.request.BookCatalogueId;
import in.viato.app.http.models.response.BookDetail;
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
                .addConverterFactory(new ToStringConverterFactory())
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

    public Observable<BookItem> addToRead(String catalogueId){
        BookCatalogueId book = new BookCatalogueId();
        book.setCatalogueId(catalogueId);

        return mViatoService
                .addToRead(book)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> removeFromRead(String readId){
        return mViatoService
                .removeFromRead(readId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<BookItem>> getWishlist(){
        return mViatoService
                .getWishlist()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<BookItem> addToWishlist(String catalogueId) {
        BookCatalogueId book = new BookCatalogueId();
        book.setCatalogueId(catalogueId);

        return mViatoService.addToWishlist(book)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> removeFromWishlist(String wishlistId) {
        return mViatoService
                .removeFromWishlist(wishlistId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<BookDetail> getBookDetail(String id) {
        return mViatoService
                .getBookDetail(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
