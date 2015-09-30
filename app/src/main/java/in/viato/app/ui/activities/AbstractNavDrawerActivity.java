package in.viato.app.ui.activities;

import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import in.viato.app.R;

/**
 * Created by ranadeep on 14/09/15.
 */
public class AbstractNavDrawerActivity extends AbstractActivity {

    @Bind(R.id.cover_image) ImageView coverImage;
    @Bind(R.id.stub_cover_image) ViewStub stubCoverImage;
    @Bind(R.id.navdrawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) protected NavigationView mNavigationView;

//    @Bind(R.id.collapsing_toolbar) protected CollapsingToolbarLayout mCollapseToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Initialise here, because ButterKnife.bind is called in AbstractActivity
        //Set overflow icon, menu, menu click listener, logo drawable ..
//        mCollapseToolbar.setTitle(mToolbar.getTitle());
//        mCollapseToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent))
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        setupDrawerContent(mNavigationView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true; //Return here because AbstractActivity catches the same android.R.id.home for doing upNavigation
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        //TODO use swtich case start apt activity
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home :
                                Snackbar.make(mDrawerLayout,"Home", Snackbar.LENGTH_LONG);
                                    break;
                            case R.id.nav_my_books :
                                break;
                            case R.id.nav_lends :
                                break;
                            case R.id.nav_profile :
                                break;
                            case R.id.nav_wallet :
                                break;
                            case R.id.nav_notifications :
                                break;
                            case R.id.nav_help :
                                break;

                            default:
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    protected void showCoverImage(){

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        View coverContainer = stubCoverImage.inflate();
        coverImage = (ImageView) coverContainer.findViewById(R.id.cover_image);
        coverImage.setVisibility(View.VISIBLE);
    }

}
