package in.viato.app.ui.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.SearchResultItem;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Subscriber;

/**
 * Created by ranadeep on 23/09/15.
 */
public class BookSearchActivity extends AbstractActivity {

    private View mResultsView;
    private SearchView mSearchView;

    private BetterViewAnimator container;
    private CardView scanButton;
    private RecyclerView list;

    private static final int REQUEST_SCAN_BARCODE = 1;
    private SearchResultAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);

        mResultsView = ((ViewStub) findViewById(R.id.stub_import)).inflate();
        container = (BetterViewAnimator) mResultsView;
        scanButton = (CardView) mResultsView.findViewById(R.id.button_scan_barcode);
        list = (RecyclerView) mResultsView.findViewById(R.id.search_results_list);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mAdapter = new SearchResultAdapter();
        list.setAdapter(mAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBarcode();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.menu_search, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        mSearchView.setIconifiedByDefault(true);
        mSearchView.setFocusable(true);
        mSearchView.setIconified(false);
        mSearchView.requestFocusFromTouch();

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.clear();
                return false;
            }
        });

        //TODO Listen to query text change, debounce and provide realtime search
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Logger.d("Entered : %s", query);
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Logger.d("Typing : %s", newText);
//                return false;
//            }
//        });

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.menu_barcode){
            scanBarcode();
        }

        return true;

    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search

            Logger.d("Query is %s", query);
            performQuery(query);
        }
        else {

        }
    }

    private void scanBarcode(){
        startActivityForResult(new Intent(this, BarcodeScannerActivity.class), REQUEST_SCAN_BARCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SCAN_BARCODE && resultCode == RESULT_OK){
            String isbn = data.getStringExtra("isbn");
            mSearchView.setQuery(isbn, true);
            Logger.d("ISBN received : %s", isbn);
        }
    }

    private void performQuery(String query){

        container.setDisplayedChildId(R.id.search_books_loading);
        mViatoAPI
                .search(query)
                .subscribe(new Subscriber<List<SearchResultItem>>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("Query completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d(e.getMessage());
                        container.setDisplayedChildId(R.id.search_books_error);
                    }

                    @Override
                    public void onNext(List<SearchResultItem> searchResultItems) {
                        if (searchResultItems != null && searchResultItems.size() != 0) {
                            container.setDisplayedChildId(R.id.search_results_list);
                            mAdapter.setItems(searchResultItems);
                        } else container.setDisplayedChildId(R.id.search_books_empty);
                    }
                });

    }

    public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultItemHolder>{

        private List<SearchResultItem> results = new ArrayList<>();

        public void setItems(List<SearchResultItem> items){
            this.results = items;
            notifyDataSetChanged();
        }

        public void clear(){
            this.results.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return results.size();
        }

        @Override
        public void onBindViewHolder(ResultItemHolder holder, int position) {
            SearchResultItem result = results.get(position);
            holder.title.setText(result.getTitle());
            holder.author.setText(result.getAuthor());

            Picasso.with(holder.itemView.getContext())
                    .load(result.getCover())
                    .into(holder.cover);
        }

        @Override
        public ResultItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.holder_search_result, parent, false);
            return new ResultItemHolder(v);
        }

        public class ResultItemHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.search_result_cover) ImageView cover;
            @Bind(R.id.search_result_title) TextView title;
            @Bind(R.id.search_result_author) TextView author;

            public ResultItemHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
