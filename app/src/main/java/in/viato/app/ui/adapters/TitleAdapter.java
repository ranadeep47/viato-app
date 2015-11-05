package in.viato.app.ui.adapters;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.http.models.response.BookItem;
import in.viato.app.http.models.response.Rental;

/**
 * Created by saiteja on 03/11/15.
 */
public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {
    private List<Rental> mRentals;

    public TitleAdapter(List<Rental> rentals) {
        super();
        this.mRentals = rentals;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.title) TextView mTitle;
        @Bind(R.id.author) TextView mAuthor;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.holder_title_author, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Rental rental = mRentals.get(position);
        holder.mTitle.setText(rental.getItem().getTitle());
        holder.mAuthor.setText(rental.getItem().getAuthors());
    }

    @Override
    public int getItemCount() {
        return mRentals.size();
    }
}