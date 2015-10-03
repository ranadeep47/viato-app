package in.viato.app.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.viato.app.R;
import in.viato.app.ui.fragments.AddressListFragment;

/**
 * Created by saiteja on 20/09/15.
 */
public class AddressListActivity extends AbstractActivity {
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                //         fragmentManager.popBackStack("Main", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                //         fragmentManager.popBackStack();
                onBackPressed();
            }
        });

        loadFragment(R.id.frame_content, AddressListFragment.newInstance("abc", "def"), "AddressListFragment", false, "AddressListFragment");
    }
}
