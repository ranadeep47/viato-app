package in.viato.app.http.clients.viato;

import android.content.res.Resources;

import java.util.List;
import java.util.Map;

import in.viato.app.R;
import in.viato.app.http.models.Address;
import in.viato.app.http.models.ForceUpdate;
import in.viato.app.http.models.Locality;
import in.viato.app.http.models.request.BookCatalogueId;
import in.viato.app.http.models.request.BookingBody;
import in.viato.app.http.models.request.CartItem;
import in.viato.app.http.models.request.RentalBody;
import in.viato.app.http.models.response.BookDetail;
import in.viato.app.http.models.response.Booking;
import in.viato.app.http.models.response.Cart;
import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.http.models.response.CategoryItem;
import in.viato.app.http.models.response.MyBooksReadResponse;
import in.viato.app.http.models.response.Serviceability;
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by ranadeep on 13/09/15.
 */
public interface ViatoService {

    String baseUrl = Resources.getSystem().getString(R.string.base_api_url);

    @GET("feed/home")
    Observable<List<CategoryItem>> getCategories();

    @GET("feed/trending")
    Observable<CategoryGrid> getTrending (@Query("page") int page);

    @GET("feed/category/{categoryId}")
    Observable<CategoryGrid> getBooksByCategory(
            @Path("categoryId") String categoryId,
            @Query("page") int page);

    @GET("search")
    Observable<List<BookItem>> search(@Query("q") String query);

    @GET("search/suggest")
    Observable<Response<List<String>>> getSuggestions(@Query("q") String query);

    @GET("user/mybooks/home")
    Observable<List<CoverQuote>> getQuotes();

    @GET("user/mybooks/read")
    Observable<MyBooksReadResponse> getRead();

    @POST("user/mybooks/read")
    Observable<BookItem> addToRead(@Body BookCatalogueId book);

    @DELETE("user/mybooks/read/{readId}")
    Observable<String> removeFromRead(@Path("readId") String readId);

    @GET("user/mybooks/wishlist")
    Observable<List<BookItem>> getWishlist();

    @POST("user/mybooks/wishlist")
    Observable<BookItem> addToWishlist(@Body BookCatalogueId book);

    @DELETE("user/mybooks/wishlist/{wishlistId}")
    Observable<Response<String>> removeFromWishlist(@Path("wishlistId") String wishlistId);

    @GET("books/{bookId}")
    Observable<BookDetail> getBookDetail(@Path("bookId") String bookId);

    @GET("user/address")
    Observable<List<Address>> getAddresses();

    @POST("user/address")
    Observable<Address> createAddress(
            @Body Address address
    );

    @PUT("user/address/{addressId}")
    Observable<Address> updateAddress(
            @Path("addressId") String addressId,
            @Body Address address
    );

    @DELETE("user/address/{addressId}")
    Observable<String> deleteAddress(@Path("addressId") String addressId);

    @GET("user/cart")
    Observable<Cart> getCart();

    @POST("user/cart")
    Observable<Response<Cart>> addToCart(@Body CartItem cartItem);

    @DELETE("user/cart/{id}")
    Observable<String> removeFromCart(@Path("id") String id);

    @GET("user/bookings")
    Observable<Response<List<Booking>>> getBookings();

    @POST("user/bookings")
    Observable<Response<String>> placeOrder(@Body BookingBody booking);

    @GET("user/bookings/{id}")
    Observable<Response<Booking>> getBooking(@Path("id") String id);

    @POST("user/bookings/rents/extend")
    Observable<Response<String>> extendRental(@Body RentalBody rentalBody);

    @POST("user/bookings/rents/return")
    Observable<Response<String>> returnRental(@Body RentalBody rentalBody);

    @GET("user/address/locality")
    Observable<Locality> getLocality(
            @Query("lat") String lat,
            @Query("lon") String lon
    );

    @GET("user/mybooks/wishlist/status/{id}")
    Observable<Response<String>> isInWishList(@Path("id") String id);

//    @GET("test")
//    Observable<MyDate> getDate();

    @GET("geo/supported/status")
    Observable<Response<Serviceability>> getServiceability(
            @Query("lat") String lat,
            @Query("lon") String lon
    );

    @GET("geo/supported/all")
    Observable<Response<List<String>>> getServiceLocalities();

    @GET("version/check")
    Observable<Response<ForceUpdate>> checkForceUpdate(
        @Query("build") String build,
        @Query("version") int version
    );
}
