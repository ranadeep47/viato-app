package in.viato.app.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;

import in.viato.app.http.models.response.BookItem;
import in.viato.app.ui.activities.BookDetailActivity;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksGirdAdapter extends RecyclerView.Adapter<MyBooksGirdAdapter.MyBooksItemHolder> {

    AdapterListener listener;

    private List<BookItem> books = new ArrayList<>();
    private String redirectType;
    private Context mContext;
    private String listType;

    public MyBooksGirdAdapter(AdapterListener listener) {
        super();
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void addAll(List<BookItem> books){
        this.books = books;
        notifyDataSetChanged();

    }

    public BookItem get(int index) {
        return this.books.get(index);
    }

    public void add(BookItem book) {
        books.add(book);
        notifyItemInserted(books.size() - 1);
        notifyDataSetChanged();
    }

    public void addToFront(BookItem book) {
        books.add(0, book);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    public String getStringId(int position) {
        return books.get(position).getCatalogueId();
    }

    public void remove(int index) {
        books.remove(index);
        notifyItemRemoved(index);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyBooksItemHolder holder, final int position) {
        final BookItem book = books.get(position);

        holder.title.setText(book.getTitle());
        Picasso.with(holder.itemView.getContext())
                .load(book.getThumbs().get(0))
                .into(holder.cover);

        holder.more.setTag(getStringId(position));
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final PopupMenu popup = new PopupMenu(mContext, v);
                popup.getMenuInflater().inflate(R.menu.menu_my_books, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String id = (String) v.getTag();
                        switch (item.getItemId()) {
//                            case R.id.menu_rate:
//                                Intent intent = new Intent(mContext, BookDetailActivity.class);
//                                intent.putExtra(BookDetailActivity.ARG_BOOK_ID, id);
//                                intent.putExtra(BookDetailActivity.ARG_RATE_BOOK, true);
//                                mContext.startActivity(intent);
//                                break;
//                            case R.id.menu_move_to:
//                                break;
                            case R.id.menu_remove:
                                listener.onBookRemove(position);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        //TODO set onClick event listener for itemView
        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.ARG_BOOK_ID, book.getCatalogueId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public MyBooksItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.holder_my_book, parent, false);

        return new MyBooksItemHolder(v);
    }

    public static class MyBooksItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.mybook_cover) ImageView cover;
        @Bind(R.id.mybook_title) TextView title;
        @Bind(R.id.icn_more) ImageView more;

        public MyBooksItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface AdapterListener {
        public void onBookRemove(int position);
    }
}
