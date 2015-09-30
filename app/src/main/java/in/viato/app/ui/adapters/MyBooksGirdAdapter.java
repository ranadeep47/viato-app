package in.viato.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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
import in.viato.app.http.models.response.MyBook;

/**
 * Created by ranadeep on 25/09/15.
 */
public class MyBooksGirdAdapter extends RecyclerView.Adapter<MyBooksGirdAdapter.MyBooksItemHolder> {

    private List<MyBook> books = new ArrayList<>();
    private String redirectType;

    public MyBooksGirdAdapter() {
        super();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void addAll(List<MyBook> books){
        this.books = books;
        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(final MyBooksItemHolder holder, int position) {
        final MyBook book = books.get(position);

        holder.title.setText(book.getTitle());
        Picasso.with(holder.itemView.getContext())
                .load(book.getCover())
                .into(holder.cover);

        //TODO set onClick event listener for itemView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(holder.itemView.getContext(), book.getExtraId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public MyBooksItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.holder_my_book, parent, false);

        return new MyBooksItemHolder(v);
    }

    public static class MyBooksItemHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.mybook_cover) ImageView cover;
        @Bind(R.id.mybook_title) TextView title;

        public MyBooksItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
