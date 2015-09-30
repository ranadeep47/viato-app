package in.viato.app.ui.activties;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import in.viato.app.R;
import in.viato.app.ui.fragments.BookDetailFragment;

public class CheckoutActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);

        loadFragment(R.id.frame_content, CheckoutActivityFragment.newInstance("abc", "def"), "CheckoutActivityFragment", false, "CheckoutActivityFragment");

//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.frame_content);
//        if(fragment == null) {
//            fragment = new CheckoutActivityFragment();
//            fm.beginTransaction()
//                    .add(R.id.frame_content, fragment)
//                    .commit();
//        }
    }
}
