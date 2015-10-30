package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import in.viato.app.R;
import in.viato.app.ui.fragments.CategoryBooksFragment;

public class CategoryBooksActivity extends AbstractActivity {

    public static final String ARG_CATEGORY_ID = "categoryId";
    public static final String ARG_CATEGORY_NAME = "categoryName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();
        String id = intent.getStringExtra(ARG_CATEGORY_ID);
        CategoryBooksFragment fragment = CategoryBooksFragment.newInstance(id);
        loadFragment(R.id.frame_content, fragment, CategoryBooksFragment.TAG, false, CategoryBooksFragment.TAG);
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
}
