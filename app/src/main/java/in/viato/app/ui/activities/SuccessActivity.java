package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.NestedScrollView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.viato.app.R;

public class SuccessActivity extends AbstractActivity {

    public static final String ARG_ORDER_ID = "orderId";
    public static final String ARG_DELIVERY_DATE = "delivery_date";

    private String orderId = "";
    private String deliveryDate;

    @Bind(R.id.order_id) TextView orderIdTV;
    @Bind(R.id.delivery_date) TextView deliveryDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);
        Intent intent = getIntent();
        if (intent == null){
            return;
        }
        orderId = intent.getStringExtra(ARG_ORDER_ID);
        deliveryDate = intent.getStringExtra(ARG_DELIVERY_DATE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FrameLayout view =  (FrameLayout) findViewById(R.id.frame_content);
        NestedScrollView view1 = (NestedScrollView) getLayoutInflater().inflate(R.layout.activity_success, null);
        view.addView(view1);

        ButterKnife.bind(this, view);

        orderIdTV.setText(orderId);
        deliveryDateTV.setText(deliveryDate);
    }

    @OnClick(R.id.trendingBooks)
    public void openTrendingBooks() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(HomeActivity.EXTRA_SELECT_TAB, HomeActivity.TAB_TRENDING);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.letShare)
    public void letShare() {
        Toast.makeText(this, "Hi, If you like the experience, Please let your friends know about us. Thank you", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mViatoApp.trackScreenView(getString(R.string.booking_detail_fragment));
//        Analytics.with(this).screen("screen", getString(R.string.booking_detail_fragment));

    }
}
