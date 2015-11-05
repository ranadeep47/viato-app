package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.utils.SharedPrefHelper;

/**
 * Created by ranadeep on 14/09/15.
 */
public class AbstractNavDrawerActivity extends AbstractActivity {

    ImageView coverImage;
    @Bind(R.id.navdrawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) protected NavigationView mNavigationView;
    @Bind(R.id.main_container) CoordinatorLayout mCoordinatorLayout ;

    private final AbstractActivity mActivity = this;

    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

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
        final int selfItem = getSelfNavDrawerItem();

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);

        navigationView
                .getMenu()
                .getItem(selfItem)
                .setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {
                if(menuItem.getOrder() == selfItem) {
                    mDrawerLayout.closeDrawers();
                    return true;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = null;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                intent = new Intent(mActivity, HomeActivity.class);
                                startActivity(intent);
                                finish();
                                break;
                            case R.id.nav_my_books:
                                intent = new Intent(mActivity, MyBooksActivity.class);
                                createBackStack(intent);
                                break;
                            case R.id.nav_lends:
                                intent = new Intent(mActivity, PreviousOrders.class);
                                createBackStack(intent);
                                break;
//                            case R.id.nav_notifications:
//                                intent = new Intent(mActivity, NotificationsActivity.class);
//                                createBackStack(intent);
//                                break;
                            case R.id.nav_help:
                                sendEmail();
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

    protected int getSelfNavDrawerItem() {
        return getResources().getInteger(R.integer.nav_item_home);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Enables back navigation for activities that are launched from the NavBar. See
     * {@code AndroidManifest.xml} to find out the parent activity names for each activity.
     * @param intent
     */
    private void createBackStack(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TaskStackBuilder builder = TaskStackBuilder.create(this);
            builder.addNextIntentWithParentStack(intent);
            builder.startActivities();
        } else {
            startActivity(intent);
            finish();
        }
    }

    public void sendEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        String phone = SharedPrefHelper.getString(R.string.pref_mobile_number);
        String text = "[Phone: " + phone + "]";
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"captain.viato@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT,  text);
        i.putExtra(Intent.EXTRA_TEXT   , "");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
