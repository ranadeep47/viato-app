package in.viato.app.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import in.viato.app.R;
import in.viato.app.http.models.response.CoverQuote;

import in.viato.app.ui.fragments.BooksReadFragment;
import in.viato.app.ui.fragments.BooksWishlistFragment;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import rx.Subscriber;

/**
 * Created by ranadeep on 24/09/15.
 */
public class MyBooksActivity extends AbstractNavDrawerActivity {

    private ViewPager mViewPager;
    private TabLayout mTabs;

    private ImageView cover;
    private TextView quote;
    private TextView quoter;

    private FloatingActionButton fab;

    private static final int REQUEST_BOOK_ID = 0;
    private static final String ARG_CURRENT_FRAGMENT = "currentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_tab_layout);

        mViewPager = (ViewPager)((ViewStub) findViewById(R.id.stub_viewpager_my_books)).inflate();
        mTabs = (TabLayout)((ViewStub) findViewById(R.id.stub_tabs_my_books)).inflate();

        View coverContainer = ((ViewStub) findViewById(R.id.stub_cover_quote)).inflate();
        cover = (ImageView)coverContainer.findViewById(R.id.cover_quote_image);
        quote = (TextView) coverContainer.findViewById(R.id.cover_quote);
        quoter = (TextView) coverContainer.findViewById(R.id.cover_quote_quoter);

        fab = (FloatingActionButton)((ViewStub) findViewById(R.id.stub_fab_add_books)).inflate();

        setupViewPager();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setTitle("");

        mViatoAPI.getQuotes().subscribe(new Subscriber<List<CoverQuote>>() {
            @Override
            public void onCompleted() {
                Logger.d("Quotes loaded");
            }

            @Override
            public void onError(Throwable e) {
                Logger.d(e.getMessage());
            }

            @Override
            public void onNext(final List<CoverQuote> coverQuotes) {

                setCoverQuote(coverQuotes.get(0));
                setupFab(getFragmentName(0));

                mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        setCoverQuote(coverQuotes.get(position));
                        setupFab(getFragmentName(position));
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });
    }

    private void setupViewPager(){
        Adapter mAdapter = new Adapter(getSupportFragmentManager());
        mAdapter.add(new BooksReadFragment(), "I Read");
        mAdapter.add(new BooksWishlistFragment(), "Wishlist");
//        mAdapter.add(new BooksOwnFragment(), "I Own");
        mViewPager.setAdapter(mAdapter);
        mTabs.setupWithViewPager(mViewPager);
//        mTabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
    }

    private String getFragmentName(int position){
        String name = "";
        switch (position) {
            case 0 :
                name = BooksReadFragment.TAG;
                break;
            case 1 :
                name = BooksWishlistFragment.TAG;
                break;
        }

        return name;
    }

    private void setupFab(final String fragmentName){
        fab.setOnClickListener(null);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BookSearchActivity.class);
                intent.putExtra(BookSearchActivity.ARG_ACTION_TO_ADD, true);
                intent.putExtra(SearchManager.QUERY, "");
                intent.putExtra(ARG_CURRENT_FRAGMENT, mViewPager.getCurrentItem());
                intent.setAction("android.intent.action.SEARCH");
                startActivityForResult(intent, REQUEST_BOOK_ID);
            }
        });
    }

    private void setCoverQuote(final CoverQuote coverQuote){
        //try from disk, else fetch from network
        final Context context = this;
        Picasso.with(this)
                .load(coverQuote.getCover())
                .transform(new ColorFilterTransformation(R.color.black))
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(cover, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(coverQuote.getCover())
                                .transform(new ColorFilterTransformation(R.color.black))
                                .into(cover);
                    }
                });

        quote.setText(coverQuote.getQuote());
        quoter.setText(coverQuote.getQuoter());
    }

    static class Adapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void add(Fragment fragment, String title){
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Adapter adapter = (Adapter) mViewPager.getAdapter();

        if (resultCode == RESULT_OK) {
            if(requestCode == REQUEST_BOOK_ID) {
                int currentFragment = data.getIntExtra(ARG_CURRENT_FRAGMENT, 0);
                mViewPager.setCurrentItem(currentFragment);
                Fragment fragment = adapter.getItem(currentFragment);
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
