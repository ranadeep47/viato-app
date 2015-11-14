package in.viato.app.http.clients.viato;

import com.squareup.moshi.Moshi;
import com.squareup.okhttp.OkHttpClient;

import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.ClientUtils;
import in.viato.app.http.clients.Rfc3339DateJsonAdapter;
import in.viato.app.http.clients.ToStringConverterFactory;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.ForceUpdate;
import in.viato.app.http.models.Locality;
import in.viato.app.http.models.request.BookCatalogueId;
import in.viato.app.http.models.request.BookingBody;
import in.viato.app.http.models.request.CartItem;
import in.viato.app.http.models.request.RentalBody;
import in.viato.app.http.models.response.BookDetail;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Cart;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.http.models.response.CategoryItem;
import in.viato.app.http.models.response.Serviceability;
import retrofit.MoshiConverterFactory;
import retrofit.Response;
import retrofit.Result;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import java.util.Date;
import java.util.List;

import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.MyBooksReadResponse;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
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

        Moshi moshi = new Moshi.Builder().add(Date.class, new Rfc3339DateJsonAdapter()).build();

        Retrofit retrofit = new Retrofit
                .Builder()
                .baseUrl(ViatoService.baseUrl)
                .client(client)
                .validateEagerly()
                .addConverterFactory(new ToStringConverterFactory())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
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

    public Observable<CategoryGrid> getTrending(int page){
        return mViatoService
                .getTrending(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<BookItem>> search(String query){
        return mViatoService
                .search(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<Response<List<String>>> getSuggestions(String query){
        return mViatoService
                .getSuggestions(query)
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

    public Observable<Response<String>> removeFromWishlist(String wishlistId) {
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

    public Observable<List<Address>> getAddress(){
        return mViatoService
                .getAddresses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<Address> createAddress(Address address) {
        return mViatoService
                .createAddress(address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<Address> updateAddress(String id, Address address) {
        return mViatoService
                .updateAddress(id, address)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public Observable<String> deleteAddress(String id) {
        return mViatoService
                .deleteAddress(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Cart> getCart() {
        return mViatoService
                .getCart()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<Cart>> addToCart(CartItem cartItem) {
        return mViatoService
                .addToCart(cartItem)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<String> removeFromCart(String id) {
        return mViatoService
                .removeFromCart(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<String>> placeOrder(BookingBody booking) {
        return mViatoService.placeOrder(booking)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

//    public Observable<MyDate> getDate() {
//        return mViatoService
//                .getDate()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io());
//    }

    public Observable<Response<List<Booking>>> getBookings() {
        return mViatoService
                .getBookings()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<Booking>> getBookingDetail(String id) {
        return mViatoService
                .getBooking(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<String>> extendRental(RentalBody body) {
        return mViatoService
                .extendRental(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<String>> returnRental(RentalBody body) {
        return mViatoService
                .returnRental(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Locality> getLocality(String latitude, String longitude) {
        return mViatoService
                .getLocality(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<String>> isInWishList(String id) {
        return mViatoService
                .isInWishList(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<Serviceability>> getServiceability(String latitude, String longitude) {
        return  mViatoService.getServiceability(latitude, longitude)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<List<String>>> getServiceLocations() {
        return  mViatoService.getServiceLocalities()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Response<ForceUpdate>> checkForceUpdate(String build, int version) {
        return mViatoService.checkForceUpdate(build, version)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
