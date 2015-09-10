package in.viato.app.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.MyBooksOwnResponse;
import in.viato.app.ui.adapters.MyBooksGirdAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.utils.ui.WrappableGridLayoutManager;
import rx.Subscriber;

/**
 * Created by ranadeep on 24/09/15.
 */
public class BooksOwnFragment extends AbstractFragment {

    public static final String TAG = "BooksOwnFragment";

    @Bind(R.id.books_own_animator) BetterViewAnimator container;

    @Bind(R.id.books_own_shared_container) LinearLayout sharedContainer;
    @Bind(R.id.books_own_not_shared_container) LinearLayout notSharedContainer;

    @Bind(R.id.books_own_shared_grid) RecyclerView sharedGrid;
    @Bind(R.id.books_own_not_shared_grid) RecyclerView notSharedGrid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books_own, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getOwned().subscribe(new Subscriber<MyBooksOwnResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
                container.setDisplayedChildId(R.id.books_own_error);

            }

            @Override
            public void onNext(MyBooksOwnResponse myBooksOwnResponse) {
                setupGrids(myBooksOwnResponse);
            }
        });
    }

    private void setupGrids(MyBooksOwnResponse mybooks){
        if(mybooks.getShared().size() == 0 &&
                mybooks.getNot_shared().size() == 0) {
            container.setDisplayedChildId(R.id.books_own_empty);
        }
        else {
            if(mybooks.getShared().size() == 0){
                sharedContainer.setVisibility(View.GONE);
            }
            else {
                MyBooksGirdAdapter sharedBooksAdapter = new MyBooksGirdAdapter();
                sharedBooksAdapter.addAll(mybooks.getShared());

                sharedGrid.setLayoutManager(new WrappableGridLayoutManager(getContext(), 3, 100));
                sharedGrid.setAdapter(sharedBooksAdapter);
            }

            if(mybooks.getNot_shared().size() == 0){
                notSharedContainer.setVisibility(View.GONE);
            }
            else {
                MyBooksGirdAdapter notSharedBooksAdapter = new MyBooksGirdAdapter();
                notSharedBooksAdapter.addAll(mybooks.getNot_shared());

                notSharedGrid.setLayoutManager(new WrappableGridLayoutManager(getContext(), 3, 200));
                notSharedGrid.setAdapter(notSharedBooksAdapter);
            }
            container.setDisplayedChildId(R.id.books_own_grids_container);
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
}
