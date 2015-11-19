package in.viato.app.ui.activities;

import android.os.Bundle;
import android.app.Activity;

import in.viato.app.R;
import in.viato.app.http.models.Locality;
import in.viato.app.ui.fragments.LocalityFragment;

public class LocalityActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        loadFragment(R.id.frame_content, LocalityFragment.newInstance(), LocalityFragment.TAG, false, LocalityFragment.TAG);
    }
}
