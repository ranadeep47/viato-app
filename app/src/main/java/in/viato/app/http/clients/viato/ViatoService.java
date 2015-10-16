package in.viato.app.http.clients.viato;

import java.util.List;

import in.viato.app.http.models.old.Book;
import in.viato.app.http.models.old.CoverQuote;
import in.viato.app.http.models.old.MyBooksOwnResponse;
import in.viato.app.http.models.old.MyBooksReadResponse;
import in.viato.app.http.models.old.MyBooksWishlistResponse;
import in.viato.app.http.models.old.SearchResultItem;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.http.models.response.CategoryItem;
import retrofit.http.GET;
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

    @GET("/search")
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
