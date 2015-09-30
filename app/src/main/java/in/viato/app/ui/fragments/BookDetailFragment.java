package in.viato.app.ui.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.models.Review;
import in.viato.app.ui.activties.AbstractActivity;
import in.viato.app.ui.activties.CheckoutActivity;
import in.viato.app.ui.adapters.RelatedBooksRVAdapter;
import in.viato.app.ui.adapters.ReviewRVAdapter;
import in.viato.app.ui.widgets.MyHorizantalLlm;
import in.viato.app.ui.widgets.MyVerticalLlm;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends AbstractFragment {

    private final String TAG = this.getClass().getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String DIALOG_RATING = "dialog_rating";

    private static final int REQUEST_RATING = 0;

    private String mParam1;
    private String mParam2;

    private static Boolean inWishlist = true;
    private static Boolean isFullDesc = true;

    public static final String EXTRA_ID = "boook_id";

    private AbstractActivity mActivity;

    @Bind(R.id.anim_toolbar) Toolbar mToolbar;

    @Bind(R.id.collapsing_toolbar) CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.img_header) ImageView mImageView;

    @Bind(R.id.fab) FloatingActionButton mFloatingActionButton;

    @Bind(R.id.user_review) TextView mUserReview;
    @Bind(R.id.desc) TextView mDescription;
    @Bind(R.id.sale_price) TextView mSalePrice;

    @Bind(R.id.user_rating) RatingBar mUserRating;

    @Bind(R.id.related_books_list) RecyclerView mRelatedBooksRV;
    @Bind(R.id.review_list_small) RecyclerView mReviewList;

    @Bind(R.id.all_reviews)
    LinearLayout allReviews;

    public static BookDetailFragment newInstance(String param1, String param2) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mActivity = getHostingActivity();
        setPalleteColors();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_book_detail, container, false);

        setHasOptionsMenu(true);

        ButterKnife.bind(this, view);

        mActivity.setSupportActionBar(mToolbar);
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mCollapsingToolbarLayout.setTitle("");
        mSalePrice.setPaintFlags(mSalePrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        setListeners();

        setupRecyclerView(mRelatedBooksRV);

        setupReviewRecyclerView(mReviewList);

        return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            startActivity(new Intent(mActivity, CheckoutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_RATING) {
            Float rating = data.getFloatExtra(RaingDialogFragment.EXTRA_RATING, 0);
            String review = data.getStringExtra(RaingDialogFragment.EXTRA_REVIEW).replaceAll("\\s+", " ");;
            mUserRating.setRating(rating);
            mUserReview.setText(review);
        }
    }

    @TargetApi(21)
    public void setPalleteColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_header2);

            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int mutedColor = palette.getMutedColor(getResources().getColor(R.color.primary));
                    int darkMutedColor = palette.getDarkMutedColor(getResources().getColor(R.color.primary_light));
                    mCollapsingToolbarLayout.setBackgroundColor(mutedColor);
                    mCollapsingToolbarLayout.setContentScrimColor(mutedColor);


                    mFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(mutedColor));

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

    public void setListeners() {
        mUserRating.setIsIndicator(false);
        mUserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rating = (float) Math.ceil(rating);

                String review = String.valueOf(mUserReview.getText());

                FragmentManager fm = mActivity.getSupportFragmentManager();

                RaingDialogFragment dialog = RaingDialogFragment
                        .newInstance(rating, review);
                dialog.setTargetFragment(BookDetailFragment.this, REQUEST_RATING);
                dialog.show(fm, DIALOG_RATING);
            }
        });

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //Todo: Add to cart
                startActivity(new Intent(mActivity, CheckoutActivity.class));
//                Snackbar.make(v, "Added to cart", Snackbar.LENGTH_SHORT)
//                        .setAction("Undo", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //Todo: Remove from cart
//                                Snackbar.make(v, "Removed from cart", Snackbar.LENGTH_SHORT).show();
//                            }
//                        })
//                        .show();
            }
        });
    }

    @OnClick(R.id.add_to_wishlist)
    public void toggleWishlist(Button button) {
        inWishlist =  !inWishlist;

        if (inWishlist) {
            button.setText("Added to whishlist");
        } else {
            button.setText("Add to wishlist");
        }
    }

    @OnClick(R.id.desc_more)
    public void toggleDesc(TextView view) {
        isFullDesc = ! isFullDesc;
        if(isFullDesc) {
            mDescription.setMaxLines(8);
            view.setText("Show More");
        } else {
            mDescription.setMaxLines(Integer.MAX_VALUE);
            view.setText("Show Less");
        }
    }

    @OnClick(R.id.all_reviews)
    public void showAllReviews(LinearLayout layout) {
        Log.d(TAG, "clicked");
        FragmentManager fm = mActivity.getSupportFragmentManager();
        fm.beginTransaction()
                .add(R.id.frame_content, ShowCaseReview.newInstance("abc", "abc"), "ShowCaseReviewFragment")
                .addToBackStack("ShowCaseReview")
                .commit();
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<String> links = new ArrayList<String>();
        for (int i = 0; i < 5; i++) {
            links.add("http://i.imgur.com/DvpvklR.png");
        }

        LinearLayoutManager layoutManager = new MyHorizantalLlm(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        RelatedBooksRVAdapter adapter = new RelatedBooksRVAdapter(getActivity(), links);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
//        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), LinearLayout.HORIZONTAL));
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
        List<Review> reviews = Review.get();
        mRecyclerView.setLayoutManager(new MyVerticalLlm(mRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new ReviewRVAdapter(reviews));
    }
}
