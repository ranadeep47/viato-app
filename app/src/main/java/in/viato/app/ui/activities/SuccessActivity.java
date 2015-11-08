package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.NestedScrollView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
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

    @Bind(R.id.orderid) TextView orderIdTV;
    @Bind(R.id.delivery_date) TextView deliveryDateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);

        FrameLayout view =  (FrameLayout) findViewById(R.id.frame_content);
        LinearLayout view1 = (LinearLayout) getLayoutInflater().inflate(R.layout.activity_success, null);
        view.addView(view1);

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

        orderIdTV.setText(orderId);
        deliveryDateTV.setText(deliveryDate);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(this.getResources().getColor(R.color.test_green_dark));
        }


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

        mViatoApp.sendScreenView(getString(R.string.booking_detail_fragment));
//        Analytics.with(this).screen("screen", getString(R.string.booking_detail_fragment));

    }
}
