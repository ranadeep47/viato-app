package in.viato.app.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import in.viato.app.R;

/**
 * Created by ranadeep on 14/09/15.
 */
public class AbstractNavDrawerActivity extends AbstractActivity {

    ImageView coverImage;
    @Bind(R.id.navdrawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) protected NavigationView mNavigationView;
    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout ;

    private final Context mContext = this;
    private Handler mHandler = new Handler();

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    private FragmentManager mFragmentManager;
    // @Bind(R.id.collapsing_toolbar) protected CollapsingToolbarLayout mCollapseToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = this.getSupportFragmentManager();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Initialise here, because ButterKnife.bind is called in AbstractActivity
        //Set overflow icon, menu, menu click listener, logo drawable ..
        // mCollapseToolbar.setTitle(mToolbar.getTitle());
        // mCollapseToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent))
        setupDrawerContent(mNavigationView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mFragmentManager.getBackStackEntryCount() >0){
                    getSupportFragmentManager().popBackStack();
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                return true; //Return here because AbstractActivity catches the same android.R.id.home for doing upNavigation
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = null;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                intent = new Intent(mContext, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_my_books:
                                intent = new Intent(mContext, MyBooksActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_lends:
                                intent = new Intent(mContext, PreviousOrders.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_notifications:
                                intent = new Intent(mContext, NotificationsActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_help:
                                Snackbar.make(mCoordinatorLayout, R.string.coming_soon, Snackbar.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, NAVDRAWER_LAUNCH_DELAY);
            }
        });

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mFragmentManager.getBackStackEntryCount() > 0) {
                    ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
                } else {
                    ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);
                }
            }
        });
    }
}
