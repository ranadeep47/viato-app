package in.viato.app.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewStub;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ui.fragments.HomeFragment;
import jp.wasabeef.picasso.transformations.ColorFilterTransformation;

/**
 * Created by ranadeep on 19/09/15.
 */
public class HomeActivity extends AbstractNavDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        showCoverImage();
        Picasso.with(this)
                .load("https://c2.staticflickr.com/6/5790/21790653402_0cf3b8c65e.jpg")
                .transform(new ColorFilterTransformation(R.color.primary_dark))
                .into(coverImage);

//        http://192.168.1.101:8080/image/cover

        HomeFragment fragment = HomeFragment.newInstance();
        loadFragment(R.id.frame_content, fragment, HomeFragment.TAG, true, HomeFragment.TAG);

//        CategoryBooksFragment fragment = CategoryBooksFragment.newInstance("1");
//        loadFragment(R.id.frame_content, fragment, CategoryBooksFragment.TAG, true, CategoryBooksFragment.TAG);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, BookSearchActivity.class));
                break;
            case R.id.menu_cart:
                startActivity(new Intent(this, CheckoutActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
