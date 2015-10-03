package in.viato.app.ui.activities;

import android.os.Bundle;

import in.viato.app.R;
public class CheckoutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        loadFragment(R.id.frame_content, CheckoutActivityFragment.newInstance("abc", "def"), CheckoutActivityFragment.TAG, false, CheckoutActivityFragment.TAG);
    }
}
