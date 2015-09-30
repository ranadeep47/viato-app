package in.viato.app.ui.activities;

import android.os.Bundle;

import in.viato.app.R;
import in.viato.app.ui.fragments.BookDetailFragment;

public class BookDetailActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);

        loadFragment(R.id.frame_content, BookDetailFragment.newInstance("abc", "def"), "BookDetailFragment", false, "BookDetailFragment");
    }
}
