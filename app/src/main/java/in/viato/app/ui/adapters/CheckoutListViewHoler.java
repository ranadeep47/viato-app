package in.viato.app.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import in.viato.app.databinding.FragmentCheckoutListItemBinding;
import in.viato.app.model.Book;

/**
 * Created by saiteja on 15/09/15.
 */
public class CheckoutListViewHoler extends RecyclerView.ViewHolder {
    private FragmentCheckoutListItemBinding mBindinig;

    public CheckoutListViewHoler(FragmentCheckoutListItemBinding binding) {
        super(binding.getRoot());
        mBindinig = binding;
    }

    public void bindConnection(Book book ) {
        mBindinig.setBook(book);
    }
}