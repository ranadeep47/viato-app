package in.viato.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.Book;

/**
 * Created by ranadeep on 21/09/15.
 */
public class CategoryBooksGridAdapter extends RecyclerView.Adapter<CategoryBooksGridAdapter.CategoryBookItemHolder> {

    private List<Book> mBooks = new ArrayList<>();

    @Override
    public int getItemCount() {
        return mBooks.size();
    }

    public void addAll(List<Book> books){
        mBooks.addAll(books);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(CategoryBookItemHolder holder, int position) {
        Book book = mBooks.get(position);

        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.rent.setText(book.getRent());
        holder.itemView.setTag(R.string.book_id, book.getBookId());

        Picasso.with(holder.itemView.getContext())
                .load(book.getCover())
                .into(holder.cover);
    }

    @Override
    public CategoryBookItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.holder_category_book_item, parent, false);

        return new CategoryBookItemHolder(v);
    }

    public static class CategoryBookItemHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.book_cover) ImageView cover;
        @Bind(R.id.book_title) TextView title;
        @Bind(R.id.book_author) TextView author;
        @Bind(R.id.book_rent_price) TextView rent;

        public CategoryBookItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
