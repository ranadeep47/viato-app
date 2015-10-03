package in.viato.app.ui.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import in.viato.app.R;
import in.viato.app.databinding.FragmentCheckoutListItemBinding;
import in.viato.app.model.Book;

/**
 * Created by saiteja on 15/09/15.
 */
public class CheckoutListAdapter extends RecyclerView.Adapter<CheckoutListAdapter.ViewHolder>{
    private static final String TAG = "CheckoutListAdapter";

    private List<Book> sBookList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final FragmentCheckoutListItemBinding mBindinig;

        public ViewHolder(FragmentCheckoutListItemBinding binding) {
            super(binding.getRoot());

            mBindinig = binding;
        }

        public void bindConnection(Book book ) {
            mBindinig.setBook(book);
        }
    }

    public CheckoutListAdapter(List<Book> bookList) {
        super();
        this.sBookList = bookList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentCheckoutListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.fragment_checkout_list_item, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindConnection(sBookList.get(position));
    }

    @Override
    public int getItemCount() {
        return sBookList.size();
    }
}