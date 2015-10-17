package in.viato.app.ui.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.ui.fragments.CategoryBooksFragment;
import in.viato.app.ui.fragments.HomeFragment;
import in.viato.app.utils.SharedPrefHelper;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;

/**
 * Created by ranadeep on 19/09/15.
 */
public class HomeActivity extends AbstractNavDrawerActivity {

    private ViewPager mViewPager;
    private TabLayout mTabs;
    @Bind(R.id.stub_cover_image) ViewStub stubCoverImage;

    private AbstractActivity mActivity;

    public static final int TAB_CATEGORIES = '0';
    public static final int TAB_TRENDING = '1';

    private EditText searchBar;

    public static final String EXTRA_SETECT_TAB = "select_tab";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        String access_token = SharedPrefHelper.getString(R.string.pref_access_token);

        mActivity = this;

        if(access_token == "") {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
            return;
        }

        mViewPager = (ViewPager)((ViewStub) findViewById(R.id.stub_viewpager_my_books)).inflate();
        mTabs = (TabLayout)((ViewStub) findViewById(R.id.stub_tabs_my_books)).inflate();

        setupViewPager();
        handleIntent(getIntent());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showCoverImage();
        Picasso.with(this)
                .load("https://c2.staticflickr.com/6/5790/21790653402_0cf3b8c65e.jpg")
                .transform(new ColorFilterTransformation(R.color.primary_dark))
                .into(coverImage);

//        http://192.168.1.101:8080/image/cover
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, BookSearchActivity.class));
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.scale_fade_out);
                break;
            case R.id.menu_cart:
                startActivity(new Intent(this, CheckoutActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void clearAll() {
            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(HomeFragment.newInstance(), "Category");
        adapter.addFrag(
                CategoryBooksFragment.newInstance("trending"),
                getResources().getString(R.string.trending)
        );

        mViewPager.setAdapter(adapter);
        mTabs.setupWithViewPager(mViewPager);
    }
    protected void showCoverImage(){

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        View coverContainer = stubCoverImage.inflate();
        coverImage = (ImageView) coverContainer.findViewById(R.id.cover_image);
        coverImage.setVisibility(View.VISIBLE);

        searchBar = (EditText) coverContainer.findViewById(R.id.search_bar);

        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String query = v.getText().toString();
                    Intent intent = new Intent(mActivity, BookSearchActivity.class);
                    intent.putExtra(SearchManager.QUERY, query);
                    intent.setAction("android.intent.action.SEARCH");
                    startActivity(intent);
                    searchBar.setText("");
                    return true;
                }
                return false;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Logger.d("start setSearchBarDrawables");
                setSearchBarDrawables();
            }
        });

        searchBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (searchBar.getRight() - searchBar.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        setSearchBarDrawables();
                        if(searchBar.getText().length() > 0){
                            //clear search bar
                            searchBar.setText("");
                        } else {
                            //start barcode activity
                            Logger.d("start setSearchBarDrawables");
                            startActivity(new Intent(mActivity, BarcodeScannerActivity.class));
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public void handleIntent(Intent intent) {
        if(intent == null){ return; }
        if(intent.hasExtra(EXTRA_SETECT_TAB)){
            int index = intent.getIntExtra(EXTRA_SETECT_TAB, 0);
            mViewPager.setCurrentItem(index);
        }
    }

    public void setSearchBarDrawables() {
        Logger.d(searchBar.getText().toString());
        if(searchBar.getText().toString().length() > 0) {
            searchBar.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.ic_search_black),
                    null,
                    mActivity.getResources().getDrawable(R.drawable.ic_close_black),
                    null);
        } else {
            searchBar.setCompoundDrawablesWithIntrinsicBounds(mActivity.getResources().getDrawable(R.drawable.ic_search_black),
                    null,
                    mActivity.getResources().getDrawable(R.drawable.ic_barcode_black_18dp),
                    null);

        }
    }
}
