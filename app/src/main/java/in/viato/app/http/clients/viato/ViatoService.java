package in.viato.app.http.clients.viato;

import java.util.List;

import in.viato.app.http.models.response.Book;
import in.viato.app.http.models.response.Category;
import in.viato.app.http.models.response.CoverQuote;
import in.viato.app.http.models.response.MyBooksOwnResponse;
import in.viato.app.http.models.response.MyBooksReadResponse;
import in.viato.app.http.models.response.MyBooksWishlistResponse;
import in.viato.app.http.models.response.SearchResultItem;
import in.viato.app.http.models.request.LoginBody;
import in.viato.app.http.models.response.SimpleResponse;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by ranadeep on 13/09/15.
 */
public interface ViatoService {

    String baseUrl = "http://192.168.1.101:8080";

    @POST("/accounts/login/")
    Observable<SimpleResponse> login(@Body LoginBody body); //TODO verify mime type

    @GET("/categories/")
    Observable<List<Category>> getCategories();

    @GET("/categories/{categoryId}/")
    Observable<List<Book>> getBooksByCategory(
            @Path("categoryId") String categoryId,
            @Query("page") int page);

    @GET("/search/")
    Observable<List<SearchResultItem>> search(@Query("query") String query);

    @GET("/mybooks/home/")
    Observable<List<CoverQuote>> getQuotes();

    @GET("/mybooks/own/")
    Observable<MyBooksOwnResponse> getOwned();

    @GET("/mybooks/read/")
    Observable<MyBooksReadResponse> getRead();

    @GET("/mybooks/wishlist/")
    Observable<MyBooksWishlistResponse> getWishlist();

    @GET("/book_detail/{bookId}/")
    Observable<Book> getBookDetails(
            @Path("bookId") String bookId
    );
}
