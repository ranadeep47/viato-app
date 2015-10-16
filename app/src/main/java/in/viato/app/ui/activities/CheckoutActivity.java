package in.viato.app.ui.activities;

import android.os.Bundle;
import android.view.MenuItem;

import in.viato.app.R;
import in.viato.app.ui.fragments.CheckoutFragment;

public class CheckoutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);
        loadFragment(R.id.frame_content, CheckoutFragment.newInstance(), CheckoutFragment.TAG, false, CheckoutFragment.TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true; //Rather than taking to parent activity, take to the previous activity
        }
        return super.onOptionsItemSelected(item);
    }
}
