package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import in.viato.app.R;
import in.viato.app.ui.fragments.BookDetailFragment;
import in.viato.app.ui.fragments.CategoryBooksFragment;

public class BookDetailActivity extends AbstractActivity {

    public static final String ARG_BOOK_ID = "BookId";
    public static final String ARG_BOOK_TITLE = "BookTitle";
    public static final String ARG_BOOK_AUTHOR = "BookAuthor";
    public static final String ARG_BOOK_POSTER = "BookPoster";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        Intent intent = getIntent();
        String id = intent.getStringExtra(ARG_BOOK_ID);
        String title = intent.getStringExtra(ARG_BOOK_TITLE);
        String author = intent.getStringExtra(ARG_BOOK_AUTHOR);
        String poster = intent.getStringExtra(ARG_BOOK_POSTER);

        BookDetailFragment fragment = BookDetailFragment.newInstance(id, title, author, poster);
        loadFragment(R.id.frame_content, fragment, BookDetailFragment.TAG, false, BookDetailFragment.TAG);
    }
}
