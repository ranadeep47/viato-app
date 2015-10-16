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

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.old.MyBook;
import in.viato.app.http.models.old.MyBooksReadResponse;
import in.viato.app.ui.adapters.MyBooksGirdAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Subscriber;

/**
 * Created by ranadeep on 24/09/15.
 */
public class BooksReadFragment extends AbstractFragment {

    public static final String TAG = "BooksReadFragment";

    @Bind(R.id.books_read_animator) BetterViewAnimator container;

//    @Bind(R.id.books_read_container) LinearLayout readContainer;
//    @Bind(R.id.books_reading_grid) RecyclerView readingContainer;

//    @Bind(R.id.books_read_grid) RecyclerView readGrid;
    @Bind(R.id.books_reading_grid) RecyclerView readingGrid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_books_read, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViatoAPI.getRead().subscribe(new Subscriber<MyBooksReadResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
                container.setDisplayedChildId(R.id.books_read_error);
            }

            @Override
            public void onNext(MyBooksReadResponse myBooksReadResponse) {
                setupGrids(myBooksReadResponse);
            }
        });
    }

    private void setupGrids(MyBooksReadResponse myBooksReadResponse){
        if(myBooksReadResponse.getReading().size() == 0 &&
                myBooksReadResponse.getRead().size() == 0){
            container.setDisplayedChildId(R.id.books_read_empty);
        }
        else {
            if(myBooksReadResponse.getReading().size() == 0){
                readingGrid.setVisibility(View.GONE);
            }
            else {
                final MyBooksGirdAdapter readingAdapter = new MyBooksGirdAdapter();
                readingAdapter.addAll(myBooksReadResponse.getReading());
                readingAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        if (readingAdapter.getItemCount() == 0) {
                            container.setDisplayedChildId(R.id.books_read_empty);
                        }
                    }
                });

                readingGrid.setLayoutManager(new GridLayoutManager(getContext(), 3));
                readingGrid.setAdapter(readingAdapter);
            }

//            if(myBooksReadResponse.getRead().size() == 0) {
//                readContainer.setVisibility(View.GONE);
//            }
//            else {
//                MyBooksGirdAdapter readAdapter = new MyBooksGirdAdapter();
//                readAdapter.addAll(myBooksReadResponse.getRead());
//
//                readGrid.setLayoutManager(new WrappableGridLayoutManager(getContext(), 3, 200));
//                readGrid.setAdapter(readAdapter);
//            }

            container.setDisplayedChildId(R.id.books_reading_grid);
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
        MyBooksGirdAdapter adapter = (MyBooksGirdAdapter) readingGrid.getAdapter();
        adapter.add(new MyBook());
        adapter.notifyDataSetChanged();
        readingGrid.scrollToPosition(adapter.getItemCount());
    }
}
