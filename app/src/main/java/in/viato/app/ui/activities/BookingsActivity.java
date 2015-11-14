package in.viato.app.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import in.viato.app.R;
import in.viato.app.ui.fragments.BookingsFragment;

public class BookingsActivity extends AbstractNavDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loadFragment(R.id.frame_content, BookingsFragment.newInstance(), BookingsFragment.TAG, false, BookingsFragment.TAG);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        //Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
                if (canback) {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
                } else {
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return getResources().getInteger(R.integer.nav_item_lends);
    }
}
