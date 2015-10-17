package in.viato.app.http.clients.viato;

import java.util.List;

import in.viato.app.http.models.request.BookCatalogueId;
import in.viato.app.http.models.response.BookDetail;
import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.http.models.response.CategoryItem;
import in.viato.app.http.models.response.MyBooksReadResponse;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ranadeep on 13/09/15.
 */
public interface ViatoService {

    String baseUrl = "http://viato.in/api/";

    @GET("feed/home")
    Observable<List<CategoryItem>> getCategories();

    @GET("feed/category/{categoryId}")
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

    @GET("books/{bookId}/")
    Observable<BookDetail> getBookDetail(
            @Path("bookId") String bookId
    );
}
