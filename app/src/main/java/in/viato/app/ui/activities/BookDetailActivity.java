package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import in.viato.app.R;
import in.viato.app.ui.fragments.BookDetailFragment;

public class BookDetailActivity extends AbstractActivity {

    public static final String ARG_BOOK_ID = "BookId";
    public static final String ARG_RATE_BOOK = "BookRating";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        Intent intent = getIntent();
        String id = intent.getStringExtra(ARG_BOOK_ID);

        BookDetailFragment fragment = BookDetailFragment.newInstance(id);
        loadFragment(R.id.frame_content, fragment, BookDetailFragment.TAG, false, BookDetailFragment.TAG);
    }
}
