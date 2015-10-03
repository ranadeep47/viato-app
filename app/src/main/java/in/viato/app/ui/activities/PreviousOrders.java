package in.viato.app.ui.activities;

import android.os.Bundle;
import android.app.Activity;

import in.viato.app.R;
import in.viato.app.ui.fragments.PastOrdersFragment;

public class PreviousOrders extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loadFragment(R.id.frame_content, PastOrdersFragment.newInstance("a", "s"), PastOrdersFragment.TAG, false, PastOrdersFragment.TAG);
    }
}
