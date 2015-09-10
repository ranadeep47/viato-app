package in.viato.app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.Book;
import in.viato.app.ui.adapters.CategoryBooksGridAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

/**
 * Created by ranadeep on 21/09/15.
 */

//Shows a grid of books from a category.

public class CategoryBooksFragment extends AbstractFragment{

    public static final String TAG = "CategoryBooksFragment";
    private static final String ARG_CategoryId = "category_id";
    private final int mSpanCount = 3;

    private String mCategoryId;
    private int page = 0;
    private boolean isFull = false;

    private Observable<Void> pageDetector;
    private Subscription firstFetchSub;

    private CategoryBooksGridAdapter adapter;
    private GridLayoutManager layoutManager;

    @Bind(R.id.category_books_animator) BetterViewAnimator container;
    @Bind(R.id.category_books_grid) RecyclerView grid;
    @Bind(R.id.error_title) TextView errorTitle;
    @Bind(R.id.error_message) TextView errorMessage;

    public static CategoryBooksFragment newInstance(String CategoryId) {
        Bundle args = new Bundle();
        args.putString(ARG_CategoryId, CategoryId);
        CategoryBooksFragment fragment = new CategoryBooksFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Bundle args = getArguments();
        mCategoryId = args.getString(ARG_CategoryId);
        return inflater.inflate(R.layout.fragment_category_books, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife views set. Play with them now
        adapter = new CategoryBooksGridAdapter();
        layoutManager = new GridLayoutManager(getActivity(), mSpanCount);

        grid.setAdapter(adapter);
        grid.setLayoutManager(layoutManager);
        grid.setItemAnimator(new DefaultItemAnimator());

        pageDetector = Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                grid.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    int pastVisibleItems, visibleItemCount, totalItemCount;
                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        visibleItemCount = layoutManager.getChildCount();
                        totalItemCount = layoutManager.getItemCount();
                        pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount+pastVisibleItems) >= totalItemCount) {
                            Logger.d("Wants the next page !");
                            subscriber.onNext(null);
                        }
                    }
                });
            }
        }).debounce(400, TimeUnit.MILLISECONDS);

        firstFetchSub = loadFirstPage();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setup here because it deals with mRxSubs whose lifecycle depends on fragment's Pause/Resume cycle
        setupInfiniteLoader();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        firstFetchSub.unsubscribe();
        super.onDestroy();
    }

    private Observable<List<Book>> getBooks(int page){
        return mViatoAPI.getBooksByCategory(mCategoryId, page);
    }

    private Subscription loadFirstPage(){
        container.setDisplayedChildId(R.id.category_books_loading);

        return  getBooks(page)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Book>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("First page loading completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorTitle.setText("Network Error");
                        errorMessage.setText(e.getMessage());
                        container.setDisplayedChildId(R.id.category_books_error);
                    }

                    @Override
                    public void onNext(List<Book> books) {
                        if(books == null || books.size() == 0){
                            container.setDisplayedChildId(R.id.category_books_empty);
                        }
                        else {
                            container.setDisplayedChildId(R.id.category_books_grid);
                            adapter.addAll(books);
                        }
                    }
                });
    }

    private void setupInfiniteLoader(){
        mRxSubs.add(pageDetector
                .flatMap(new Func1<Void, Observable<List<Book>>>() {
                             @Override
                             public Observable<List<Book>> call(Void aVoid) {
                                 if (!isFull) {
                                     ++page;
                                     return getBooks(page);
                                 }
                                 else return Observable.empty();
                             }
                         }
                )
                .subscribe(new Subscriber<List<Book>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("Books loaded for page %i", page);
                    }

                    @Override
                    public void onError(Throwable e) {
                        showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Book> books) {
                        if (books.size() == 0) {
                            isFull = true;
                            return;
                        }
                        adapter.addAll(books);
                    }
                }));

    }

}
