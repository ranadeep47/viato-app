package in.viato.app.ui.activties;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import in.viato.app.AddressListFragment;
import in.viato.app.R;

/**
 * Created by saiteja on 20/09/15.
 */
public class AddressListActivity extends AbstractActivity {

    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.frame_content);
        if(fragment == null) {
            fragment = new AddressListFragment();
            fm.beginTransaction()
                    .add(R.id.frame_content, fragment)
                    .commit();
        }
    }
}
