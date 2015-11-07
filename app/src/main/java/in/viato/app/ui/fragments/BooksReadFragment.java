package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.orhanobut.logger.Logger;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.MyBooksReadResponse;
import in.viato.app.ui.adapters.MyBooksGirdAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import retrofit.HttpException;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ranadeep on 24/09/15.
 */
public class BooksReadFragment extends AbstractFragment implements MyBooksGirdAdapter.AdapterListener{

    public static final String TAG = "BooksReadFragment";
    private final MyBooksGirdAdapter adapter = new MyBooksGirdAdapter(this);

    @Bind(R.id.books_read_animator) BetterViewAnimator container;
    @Bind(R.id.books_reading_grid) RecyclerView readGrid;

    private CompositeSubscription mSubs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books_read, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubs.add(mViatoAPI.getRead().subscribe(new Subscriber<MyBooksReadResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpException) {
                    handleNetworkException((HttpException) e);
                }
                container.setDisplayedChildId(R.id.books_read_error);
            }

            @Override
            public void onNext(MyBooksReadResponse myBooksReadResponse) {
                setupGrids(myBooksReadResponse);
            }
        }));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSubs.unsubscribe();
    }

    private void setupGrids(MyBooksReadResponse myBooksReadResponse){
        readGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
        readGrid.setAdapter(adapter);

        if(myBooksReadResponse.getRead().isEmpty()){
            container.setDisplayedChildId(R.id.books_read_empty);
        }
        else {
            container.setDisplayedChildId(R.id.books_reading_grid);
            adapter.addAll(myBooksReadResponse.getRead());
        }

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.getItemCount() == 0) {
                    container.setDisplayedChildId(R.id.books_read_empty);
                }
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if (positionStart == 0 && itemCount == 1) {
                    container.setDisplayedChildId(R.id.books_reading_grid);
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
        BookItem item = data.getParcelableExtra("book"); // TODO: 01/11/15 replace the string with a resource

        Product product = new Product()
                .setId(item.getCatalogueId())
                .setName(item.getTitle())
                .setCustomDimension(getActivity().getResources().getInteger(R.integer.source),
                        getActivity().getString(R.string.title_activity_my_books));

        HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                .addImpression(product, "add to read list");

        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
        t.setScreenName(getString(R.string.title_activity_my_books));
        t.send(builder.build());
        t.setScreenName(null);

        mViatoApp.trackEvent(getString(R.string.title_activity_my_books), "read_list", "add", "book", item.getCatalogueId(),"", getString(R.string.title_activity_my_books));

        mViatoAPI
                .addToRead(item.getCatalogueId())
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
                        readGrid.scrollToPosition(0);
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
                .addImpression(product, "remove from read list");

        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
        t.setScreenName(getString(R.string.title_activity_my_books));
        t.send(builder.build());
        t.setScreenName(null);

        mViatoApp.trackEvent(getString(R.string.title_activity_my_books),
                "read_list", "remove", "book", book.getCatalogueId(),"", getString(R.string.title_activity_my_books));

        mViatoAPI
                .removeFromRead(book.get_id())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Snackbar.make(container, "Error removing book from read list", Snackbar.LENGTH_LONG).show();
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(String s) {
                        Logger.d("Removed from read list");
                        adapter.remove(position);
                    }
                });

    }

}
