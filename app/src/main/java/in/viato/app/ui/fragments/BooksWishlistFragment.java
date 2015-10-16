package in.viato.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class BooksWishlistFragment extends AbstractFragment {

    public static final String TAG = "BooksWishlistFragment";

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
        if(books.size() == 0){
            container.setDisplayedChildId(R.id.books_wishlist_empty);
        }
        else {
            final MyBooksGirdAdapter adapter = new MyBooksGirdAdapter();
            adapter.addAll(books);
            adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    if(adapter.getItemCount() == 0) {
                        container.setDisplayedChildId(R.id.books_wishlist_empty);
                    }
                }
            });

            grid.setLayoutManager(new GridLayoutManager(getContext(), 3));
            grid.setAdapter(adapter);
            container.setDisplayedChildId(R.id.books_wishlist_grid);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyBooksGirdAdapter adapter = (MyBooksGirdAdapter) grid.getAdapter();
        adapter.add(new BookItem()); //TODO
        adapter.notifyDataSetChanged();
        grid.scrollToPosition(adapter.getItemCount());
    }
}
