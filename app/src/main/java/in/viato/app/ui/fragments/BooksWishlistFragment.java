package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.ui.adapters.MyBooksGirdAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Subscriber;

/**
 * Created by ranadeep on 24/09/15.
 */
public class BooksWishlistFragment extends AbstractFragment implements MyBooksGirdAdapter.AdapterListener{

    public static final String TAG = "BooksWishlistFragment";
    private final MyBooksGirdAdapter adapter = new MyBooksGirdAdapter(this);

    @Bind(R.id.books_wishlist_animator) BetterViewAnimator container;
    @Bind(R.id.books_wishlist_grid) RecyclerView grid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books_wishlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getWishlist().subscribe(new Subscriber<List<BookItem>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
                container.setDisplayedChildId(R.id.books_wishlist_error);
            }

            @Override
            public void onNext(List<BookItem> books) {
                setupGrid(books);
            }
        });
    }

    private void setupGrid(List<BookItem> books){
        grid.setLayoutManager(new GridLayoutManager(getContext(), 3));
        grid.setAdapter(adapter);

        if(books.size() == 0){
            container.setDisplayedChildId(R.id.books_wishlist_empty);
        }
        else {
            container.setDisplayedChildId(R.id.books_wishlist_grid);
            adapter.addAll(books);
        }

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() == 0) {
                    container.setDisplayedChildId(R.id.books_wishlist_empty);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    container.setDisplayedChildId(R.id.books_wishlist_grid);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        BookItem item = data.getParcelableExtra("book"); //TODO , replace the string with a resource

        Product product = new Product()
                .setId(item.getCatalogueId())
                .setName(item.getTitle())
                .setCustomDimension(getActivity().getResources().getInteger(R.integer.source),
                        getActivity().getString(R.string.title_activity_my_books));

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .addImpression(product, "add to wish list");

        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
        t.setScreenName(getString(R.string.title_activity_my_books));
        t.send(builder.build());
        t.setScreenName(null);

        mViatoApp.trackEvent(getString(R.string.title_activity_my_books),
                "wish_list", "add", "book", item.getCatalogueId(), getString(R.string.title_activity_my_books));

        mViatoAPI
                .addToWishlist(item.getCatalogueId())
                .subscribe(new Subscriber<BookItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Toast.makeText(getActivity(), "Problem adding book.", Toast.LENGTH_SHORT).show();
                        adapter.remove(0);
                    }

                    @Override
                    public void onNext(BookItem bookItem) {
                        adapter.addToFront(bookItem);
                        grid.scrollToPosition(0);
                    }
                });
    }

    @Override
    public void onBookRemove(int position) {
        removeFromList(adapter.get(position), position);

    }

    private void removeFromList(BookItem book, final int position) {
        Product product = new Product()
                .setId(book.getCatalogueId())
                .setName(book.getTitle())
                .setCustomDimension(getResources().getInteger(R.integer.source),
                        getString(R.string.title_activity_my_books));

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .addImpression(product, "remove from wish list");

        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
        t.setScreenName(getString(R.string.title_activity_my_books));
        t.send(builder.build());
        t.setScreenName(null);

        mViatoApp.trackEvent(getString(R.string.title_activity_my_books),
                "wish_list", "remove", "book", book.getCatalogueId(), getString(R.string.title_activity_my_books));

        mViatoAPI
                .removeFromWishlist(book.get_id())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("Error removing from wishlist");
                    }

                    @Override
                    public void onNext(String s) {
                        Logger.d("Removed from wishlist");
                        adapter.remove(position);
                    }
                });

    }
}
