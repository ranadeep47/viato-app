package in.viato.app.ui.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.graphics.Palette;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.orhanobut.logger.Logger;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.models.request.CartItem;
import in.viato.app.http.models.response.BookDetail;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.Cart;
import in.viato.app.model.Review;
import in.viato.app.ui.activities.AbstractActivity;
import in.viato.app.ui.activities.BookSearchActivity;
import in.viato.app.ui.activities.CheckoutActivity;
import in.viato.app.ui.adapters.RelatedBooksRVAdapter;
import in.viato.app.ui.adapters.ReviewRVAdapter;
import in.viato.app.ui.widgets.BetterViewAnimator;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.ui.widgets.MyVerticalLlm;
import retrofit.Response;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class BookDetailFragment extends AbstractFragment {

    public static final String TAG = BookDetailFragment.class.getSimpleName();

    public static final String EXTRA_ID = "book_id";

    public static final String ARG_BOOK_ID = "bookId";

    private static final String DIALOG_RATING = "dialog_rating";

    private static final int REQUEST_RATING = 0;

    private String mBookId;

    private Boolean inWishList = false;

    private static Boolean isFullDesc = false;

    private AbstractActivity mActivity;
    private Context mContext;

    private BookDetail mBookDetail;
    private CompositeSubscription mSubs = new CompositeSubscription();

    @Bind(R.id.img_header) ImageView mCover;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.author) TextView mAuthor;
    @Bind(R.id.book_rating_stars) RatingBar mBookRatingStars;
    @Bind(R.id.book_rating) TextView mBookRating;
    @Bind(R.id.retail_price) TextView mRetailPrice;
    @Bind(R.id.rental_price) TextView mRentalPrice;
    @Bind(R.id.description) TextView mDescription;
    @Bind(R.id.btn_desc_more) Button mBtn_desc_more;
    @Bind(R.id.rental_period) TextView mRentalPeriod;

    @Bind(R.id.book_detail_animator) BetterViewAnimator mAnimator;
    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.books_detail_loading) ProgressBar mProgressBar;

    @Bind(R.id.related_books_list) RecyclerView mRelatedBooksRV;
    @Bind(R.id.review_list_small) RecyclerView mReviewList;
    @Bind(R.id.user_review) TextView mUserReview;
    @Bind(R.id.user_rating) RatingBar mUserRating;
    @Bind(R.id.all_reviews) LinearLayout allReviews;
    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.fab) Button mFAB;

    public static BookDetailFragment newInstance(String id) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        ViatoApplication.get().sendScreenView(getString(R.string.book_detail_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.book_detail_fragment));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBookId = getArguments().getString(ARG_BOOK_ID);
        }

        mActivity = (AbstractActivity) getActivity();
        mContext = getContext();

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSubs.add(mViatoAPI.getBookDetail(mBookId)
                .subscribe(new Action1<BookDetail>() {
                    @Override
                    public void call(BookDetail bookDetail) {
                        mBookDetail = bookDetail;
                        setVariables(bookDetail);
                        setupWishList(bookDetail.get_id());
                        mAnimator.setDisplayedChildId(R.id.book_container);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.e(throwable.getMessage());
                    }
                }));

        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setListeners();

        setupRecyclerView(mRelatedBooksRV);

        setupReviewRecyclerView(mReviewList);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true; //Rather than taking to parent activity, take to the previous activity
            case R.id.menu_search:
                startActivity(new Intent(getActivity(), BookSearchActivity.class));
                mViatoApp.sendEvent("search", "clicked_icon", "bookDetail_menu");
                return true;
            case R.id.menu_cart:
                startActivity(new Intent(getActivity(), CheckoutActivity.class));
                mViatoApp.sendEvent("cart", "clicked_icon","bookDetail_menu");
                return true;
            case R.id.action_favorite:
                toggleFavourite();
                return true;
            //            case R.id.action_share:
//                mViatoApp.trackEvent(getString(R.string.book_detail_fragment),
//                        "share", "clicked", "icon", mBookDetail.get_id(), "bookDetail_menu");
//                letShare();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSubs.unsubscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_RATING) {
            Float rating = data.getFloatExtra(RatingDialogFragment.EXTRA_RATING, 0);
            String review = data.getStringExtra(RatingDialogFragment.EXTRA_REVIEW).replaceAll("\\s+", " ");
            mUserRating.setRating(rating);
            mUserReview.setText(review);
        }
    }

    public void setListeners() {
        mUserRating.setIsIndicator(false);
        mUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rating = (float) Math.ceil(rating);

                String review = String.valueOf(mUserReview.getText());

                FragmentManager fm = mActivity.getSupportFragmentManager();

                RatingDialogFragment dialog = RatingDialogFragment
                        .newInstance(rating, review);
                dialog.setTargetFragment(BookDetailFragment.this, REQUEST_RATING);
                dialog.show(fm, DIALOG_RATING);
            }
        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final ProgressDialog progressDialog = showProgressDialog("Adding to cart...");

                if (mBookDetail == null) {
                    return;
                }
                String catalogueId = mBookDetail.get_id();
                String rentalId = mBookDetail.getPricing().getRental().get(0).get_id();
                Boolean available = mBookDetail.getAvailable();
                float rent = mBookDetail.getPricing().getRental().get(0).getRent();

                // TODO: 01/11/15 Confirm the id
                Product product = new Product()
                        .setId(catalogueId)
                        .setName(mBookDetail.getTitle())
                        .setPrice(mBookDetail.getPricing().getRental().get(0).getRent())
                        .setQuantity(1)
                        .setCustomDimension(getResources().getInteger(R.integer.name),
                                available ? getString(R.string.available) : getString(R.string.not_available));

                ProductAction productAction = new ProductAction(ProductAction.ACTION_ADD);
                HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                        .addProduct(product)
                        .setProductAction(productAction);

                Tracker t = mViatoApp.getGoogleAnalyticsTracker();
                t.setScreenName(getString(R.string.book_detail_fragment));
                t.send(builder.build());

                if ((!available) || (rent == 0)) {
                    mViatoApp.sendEvent("cart", "cannot_add", mBookDetail.getTitle());
                    progressDialog.dismiss();
                    Snackbar.make(v, "Sorry, this book is unavailable for rental. We are updating our collection everyday.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                mViatoAPI.addToCart(new CartItem(catalogueId, rentalId))
                        .subscribe(new Subscriber<Response<Cart>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressDialog.dismiss();
//                                Logger.d("Item not added to cart " + R.string.due_to + e.getMessage());
                                mViatoApp.sendEvent("cart", "cannot_add", mBookDetail.getTitle());
                                Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNext(Response<Cart> cart) {
                                progressDialog.dismiss();
                                if (cart.isSuccess()) {
                                    startActivity(new Intent(getActivity(), CheckoutActivity.class));
                                    mViatoApp.sendEventWithCustomDimension("cart", "added", "book", R.integer.name, mBookDetail.getTitle());
                                } else {
                                    try {
                                        Snackbar.make(v, cart.errorBody().string(), Snackbar.LENGTH_LONG).show();
                                    } catch (IOException e) {
                                        Logger.e(e, "error");
                                    }
                                }
                            }
                        });

            }
        });
    }

    @OnClick(R.id.btn_desc_more)
    public void toggleDesc(TextView view) {
        if(isFullDesc) {
            mDescription.setMaxLines(7);
            view.setText(R.string.show_more);
        } else {
            mDescription.setMaxLines(Integer.MAX_VALUE);
            view.setText(R.string.show_less);
        }
        isFullDesc = ! isFullDesc;
    }

    @OnClick(R.id.all_reviews)
    public void showAllReviews() {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.frame_content, ShowCaseReview.newInstance("abc", "abc"), "ShowCaseReviewFragment")
                .addToBackStack("ShowCaseReview")
                .commit();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<String> links = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            links.add("http://i.imgur.com/DvpvklR.png");
        }

        LinearLayoutManager layoutManager = new MyHorizantalLlm(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RelatedBooksRVAdapter adapter = new RelatedBooksRVAdapter(R.layout.book_item_layout, links, true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                // Handle RecyclerView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    public void setupReviewRecyclerView(RecyclerView mRecyclerView) {
        List<Review> reviews = ((new Review()).get());
        mRecyclerView.setLayoutManager(new MyVerticalLlm(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ReviewRVAdapter(reviews));
    }


    public void letShare(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "EXTRA_TEXT");
        intent.putExtra(Intent.EXTRA_SUBJECT, "EXTRA_SUBJECT");
//        intent = Intent.createChooser(intent, getString(R.string.send_report));
        startActivity(intent);
    }

    public void setVariables(final BookDetail book)  {
        //set cover
        Picasso.with(mContext)
                .load(book.getCover())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(mCover);
        //set title
        mTitle.setText(book.getTitle());
        mCollapsingToolbarLayout.setTitle(book.getTitle());
        //set Author
        mAuthor.setText("by " + book.getAuthors());
        //set stars
        if ((int)book.getRating() != 0) {
            mBookRatingStars.setRating(book.getRating());
            mBookRating.setText(book.getRating() + "");
        } else {
            mBookRatingStars.setVisibility(View.GONE);
            mBookRating.setVisibility(View.GONE);
        }
        //set color to rating stars
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LayerDrawable stars = (LayerDrawable) mBookRatingStars.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(getActivity().getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);
        }
        //set price and strike through retail price
        if ((int) book.getPricing().getRental().get(0).getRent() != 0){
            mRetailPrice.setText("Buy at Rs. " + (int) book.getPricing().getOwning().getMrp());
            mRetailPrice.setPaintFlags(mRetailPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mRentalPrice.setText("Rent at Rs. " + (int) book.getPricing().getRental().get(0).getRent());
            mRentalPeriod.setText("for " + book.getPricing().getRental().get(0).getPeriod() + " days");
        } else {
            mRentalPrice.setVisibility(View.GONE);
            mRetailPrice.setVisibility(View.GONE);
            mRentalPeriod.setVisibility(View.GONE);
        }

        //set description
        Spanned sp = Html.fromHtml(mBookDetail.getDescription());
        mDescription.setText(sp);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int lineCount = mDescription.getLineCount();
                mBtn_desc_more.setVisibility(lineCount > 7 ? View.VISIBLE : View.GONE);
            }
        }, 500);
    }

    @TargetApi(21)
    public void setPalleteColors(ImageView imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isAdded()) {

            Bitmap myBitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

            Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    if (!isAdded()) {
                        return;
                    }
                    int mutedColor = palette.getMutedColor(getResources().getColor(R.color.primary));
                    int darkMutedColor = palette.getDarkMutedColor(getResources().getColor(R.color.primary_light));
                    mCollapsingToolbarLayout.setBackgroundColor(mutedColor);
                    mCollapsingToolbarLayout.setContentScrimColor(mutedColor);

                    mFAB.setBackgroundTintList(ColorStateList.valueOf(mutedColor));

                    Window window = mActivity.getWindow();

                    // clear FLAG_TRANSLUCENT_STATUS flag:
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                    // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                    // finally change the color
                    window.setStatusBarColor(darkMutedColor);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        mActivity = null;
        mContext = null;
    }

    public void setupWishList(String id) {
        mViatoAPI.isInWishList(id)
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        if (stringResponse.isSuccess()) {
                            inWishList = Boolean.valueOf(stringResponse.body());
                            ActionMenuItemView menuItem = (ActionMenuItemView)getActivity().findViewById(R.id.action_favorite);

                            if (inWishList){
                                menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white));
                            } else {
                                menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_outline));
                            }
                        } else {
//                            Log.e("Book", stringResponse.errorBody().string());
                        }
                    }
                });
    }

    public void addToWishlist() {
        mViatoAPI.addToWishlist(mBookDetail.get_id())
                .subscribe(new Subscriber<BookItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Snackbar.make(mCollapsingToolbarLayout, "Something went wrong please try again", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(BookItem bookItem) {
                        Toast.makeText(mContext, "Added to wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void removeFromWishlist() {
        mViatoAPI.removeFromWishlist(mBookDetail.get_id())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.e(e.getMessage());
                        Snackbar.make(mCollapsingToolbarLayout, "Something went wrong please try again", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(String s) {
                        Toast.makeText(mContext, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void toggleFavourite() {
        if (mBookDetail == null) { return; }

        Product product =  new Product()
                .setId(mBookDetail.get_id())
                .setName(mBookDetail.getTitle())
                .setPrice(mBookDetail.getPricing().getRental().get(0).getRent())
                .setQuantity(1);

        HitBuilders.ScreenViewBuilder builder;

        ActionMenuItemView menuItem = (ActionMenuItemView) getActivity().findViewById(R.id.action_favorite);

        if (inWishList) {
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_outline));
            inWishList = !inWishList;
            removeFromWishlist();
            builder = new HitBuilders.ScreenViewBuilder()
                    .addImpression(product, "removed from wish list");
            mViatoApp.sendEvent("wish_list", "remove", mBookDetail.getTitle());
        } else {
            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_favorite_white));
            inWishList = !inWishList;
            addToWishlist();
            builder = new HitBuilders.ScreenViewBuilder()
                    .addImpression(product, "added to wish list");
            mViatoApp.sendEvent("wish_list", "add", mBookDetail.getTitle());
        }

        Tracker t = mViatoApp.getGoogleAnalyticsTracker();
        t.setScreenName(getString(R.string.book_detail_fragment));
        t.send(builder.build());
    }
}
