package in.viato.app.ui.activties;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.utils.ui.ViewUtils;

/**
 * Created by ranadeep on 14/09/15.
 */
public class AbstractNavDrawerActivity extends AbstractActivity {

    @Bind(R.id.navdrawer_layout) protected DrawerLayout mDrawerLayout;
    @Bind(R.id.nav_view) protected NavigationView mNavigationView;
    @Bind(R.id.toolbar) protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Initialise here, because ButterKnife.bind is called in AbstractActivity
        //Set overflow icon, menu, menu click listener, logo drawable ..
        setSupportActionBar(mToolbar);

        final ActionBar ab = getSupportActionBar();

        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

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
                        mDrawerLayout.closeDrawers();
                        //TODO use swtich case start apt activity
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home :
                                Snackbar.make(new View(getApplicationContext()),"Home", Snackbar.LENGTH_LONG).show();
                                break;
                            case R.id.nav_my_books :
                                startActivity(new Intent(getApplicationContext(), BookDetailActivity.class));
                                overridePendingTransition(R.anim.slide_in_from_right, android.R.anim.fade_out);
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
                        return true;
                    }
                });
    }

}
