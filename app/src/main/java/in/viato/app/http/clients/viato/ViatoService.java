package in.viato.app.http.clients.viato;

import java.util.List;

import in.viato.app.http.models.Address;
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
import retrofit.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ranadeep on 13/09/15.
 */
public interface ViatoService {

    String baseUrl = "http://viato.in/api/";

    @GET("feed/home/")
    Observable<List<CategoryItem>> getCategories();

    @GET("feed/category/{categoryId}/")
    Observable<CategoryGrid> getBooksByCategory(
            @Path("categoryId") String categoryId,
            @Query("page") int page);

    @GET("search")
    Observable<List<BookItem>> search(@Query("q") String query);

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
    Observable<String> removeFromWishlist(@Path("wishlistId") String wishlistId);

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
    Observable<Cart> addToCart(@Body CartItem cartItem);

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

//    @GET("test")
//    Observable<MyDate> getDate();
}
