package in.viato.app.ui.adapters;

/**
 * Created by saiteja on 21/09/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.model.Address;

public class AddressAdapter extends BaseAdapter {
    private final Context context;
    private final List<Address> addresses;
    private int selectedPosition = 0;

    @Bind(R.id.radio_select_address) RadioButton radioButton;
    @Bind(R.id.tv_label_addressItem) TextView textViewLabel;
    @Bind(R.id.tv_street_addressItem) TextView textViewAddress;
    @Bind(R.id.tv_locality_addressItem) TextView textViewLocality;

    public AddressAdapter(Context context, List<Address> addressList) {
        this.context = context;
        this.addresses = addressList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_address_list_item, null);
        }

        ButterKnife.bind(this, convertView);

        Address address = (Address) getItem(position);
        textViewLabel.setText(address.getLabel());
        textViewAddress.setText(address.getAddress());
        textViewLocality.setText(address.getLocality());

        if(position == selectedPosition) {
            radioButton.setChecked(true);
        }
        radioButton.setTag(position);
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = (Integer) v.getTag();
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return addresses.size();
    }

    @Override
    public Address getItem(int position) {
        return addresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}