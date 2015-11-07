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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.CategoryGrid;
import in.viato.app.ui.adapters.CategoryBooksGridAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ranadeep on 21/09/15.
 */

//Shows a grid of books from a category.

public class CategoryBooksFragment extends AbstractFragment{

    public static final String TAG = CategoryBooksFragment.class.getSimpleName();

    private static final String ARG_CATEGORY_ID = "categoryId";
    private static final String ARG_CATEGORY_NAME = "categoryName";

    private final int mSpanCount = 3;

    private String mCategoryId;
    private String mCategoryName;

    private int page = 0;
    private boolean isFull = false;

    private Observable<Void> pageDetector;

    private CategoryBooksGridAdapter adapter;
    private GridLayoutManager layoutManager;

    private CompositeSubscription mSubs;

    @Bind(R.id.category_books_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.category_books_grid) RecyclerView grid;
    @Bind(R.id.error_title) TextView errorTitle;
    @Bind(R.id.error_message) TextView errorMessage;
    @Bind(R.id.paging_progress_bar) ProgressBar mProgressBar;

    public static CategoryBooksFragment newInstance(String categoryId, String categoryName) {
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
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
        mCategoryId = args.getString(ARG_CATEGORY_ID);
        mCategoryName = args.getString(ARG_CATEGORY_NAME);
        return inflater.inflate(R.layout.fragment_category_books, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife views set. Play with them now
        adapter = new CategoryBooksGridAdapter(mCategoryName);
        layoutManager = new GridLayoutManager(getActivity(), mSpanCount);

        if (mCategoryId != "trending") {
            getActivity().setTitle(mCategoryName);
        }
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

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            subscriber.onNext(null);
                        }
                    }
                });
            }
        }).debounce(400, TimeUnit.MILLISECONDS);

        mSubs.add(loadFirstPage());
        setupInfiniteLoader();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ViatoApplication.get().trackScreenView(getString(R.string.category_books_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.category_books_fragment));
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mSubs.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Observable<CategoryGrid> getBooks(int page){
        if (mCategoryId == "trending") {
            return mViatoAPI.getTrending(page);
        } else {
            return mViatoAPI.getBooksByCategory(mCategoryId, page);
        }
    }

    private Subscription loadFirstPage(){
        mAnimator.setDisplayedChildId(R.id.category_books_loading);
        return  getBooks(page)
                .subscribe(new Subscriber<CategoryGrid>() {
                    @Override
                    public void onCompleted() {
//                        Logger.d("First page loading completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorTitle.setText("Network Error");
                        errorMessage.setText(e.getMessage());
                        mAnimator.setDisplayedChildId(R.id.category_books_error);
                    }

                    @Override
                    public void onNext(CategoryGrid categoryGrid) {
                        List<BookItem> books = categoryGrid.getList();
                        if (books == null || books.size() == 0) {
                            mAnimator.setDisplayedChildId(R.id.category_books_empty);
                        } else {
                            mAnimator.setDisplayedChildId(R.id.grid_container);
                            adapter.addAll(books);
                        }
                    }
                });
    }

    private void setupInfiniteLoader(){
        mSubs.add(pageDetector
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Void, Observable<CategoryGrid>>() {
                             @Override
                             public Observable<CategoryGrid> call(Void aVoid) {
                                 if (!isFull) {
                                     toggleProgressBar();
                                     ++page;
                                     return getBooks(page);
                                 } else return Observable.empty();
                             }
                         }
                )
                .subscribe(new Subscriber<CategoryGrid>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d(e.getMessage());
                    }

                    @Override
                    public void onNext(CategoryGrid categoryGrid) {
                        toggleProgressBar();
                        List<BookItem> books = categoryGrid.getList();
                        if (books.size() == 0) {
                            isFull = true;
                            return;
                        }
                        adapter.addAll(books);
                    }
                }));

    }

    public void toggleProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

}
