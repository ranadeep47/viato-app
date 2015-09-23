package in.viato.app.ui.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Target;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;
import in.viato.app.ui.activties.AbstractActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailFragment extends AbstractFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String DIALOG_RATING = "dialog_rating";

    private static final int REQUEST_RATING = 0;

    private String mParam1;
    private String mParam2;

    private static Boolean inWishlist = true;
    private static Boolean isFullDesc = true;

    private AbstractActivity mActivity;

    @Bind(R.id.anim_toolbar)
    Toolbar mToolbar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Bind(R.id.img_header)
    ImageView mImageView;

    @Bind(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    @Bind(R.id.desc)
    TextView description;

    @Bind(R.id.user_rating)
    RatingBar userRating;

    @Bind(R.id.user_review)
    TextView userReview;

    public static BookDetailFragment newInstance(String param1, String param2) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public BookDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mActivity = getHostingActivity();
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

        mCollapsingToolbarLayout.setTitle("");

        setPalleteColors();

        setListeners();
//        description.setMovementMethod(ScrollingMovementMethod.getInstance());

        return view;

    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_book_detail;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_book_detail, menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @TargetApi(21)
    public void setPalleteColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_header);

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
        userRating.setIsIndicator(false);
        userRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rating = (float) Math.ceil(rating);

                String review = String.valueOf(userReview.getText());

                FragmentManager fm = mActivity.getSupportFragmentManager();

                RaingDialogFragment dialog = RaingDialogFragment
                        .newInstance(rating, review);
                dialog.setTargetFragment(BookDetailFragment.this, REQUEST_RATING);
                dialog.show(fm, DIALOG_RATING);
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
            description.setMaxLines(8);
            view.setText("Show More");
        } else {
            description.setMaxLines(Integer.MAX_VALUE);
            view.setText("Show Less");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_RATING) {
            Float rating = data.getFloatExtra(RaingDialogFragment.EXTRA_RATING, 0);
            String review = data.getStringExtra(RaingDialogFragment.EXTRA_REVIEW);
            userRating.setRating(rating);
            userReview.setText(review);
        }
    }
}
