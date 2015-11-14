package in.viato.app.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import in.viato.app.R;

public class SuccessActivity extends AbstractActivity {

    public static final String ARG_ORDER_ID = "orderId";
    public static final String ARG_DELIVERY_DATE = "delivery_date";
    public static final String ARG_QUANTITY = "quantity";

    private String orderId;
    private String deliveryDate;
    private int quantity;

    @Bind(R.id.orderId) TextView orderIdTV;
    @Bind(R.id.success_message) TextView messageTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bare);

        FrameLayout view =  (FrameLayout) findViewById(R.id.frame_content);
        RelativeLayout view1 = (RelativeLayout) getLayoutInflater().inflate(R.layout.activity_success, null);
        view.addView(view1);

        Intent intent = getIntent();
        if (intent == null){
            return;
        }
        orderId = intent.getStringExtra(ARG_ORDER_ID);
        deliveryDate = intent.getStringExtra(ARG_DELIVERY_DATE);
        quantity = intent.getIntExtra(ARG_QUANTITY, 1);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        orderIdTV.setText("#" + orderId.toUpperCase());
        Spanned message = Html.fromHtml("Sit back and relax.<br />Your " +
                (quantity > 1 ? "books" : "book") +
                " will be delivered by" +
                "<br /><b>" + deliveryDate + "</b>");
        messageTV.setText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(this.getResources().getColor(R.color.green_dark));
        }
    }

    @OnClick({R.id.trendingBooks, R.id.icon_success})
    public void openTrendingBooks() {
        Intent intent = new Intent(this, HomeActivity.class);
//        intent.putExtra(HomeActivity.EXTRA_SELECT_TAB, HomeActivity.TAB_TRENDING);
        startActivity(intent);
        finish();
    }

//    @OnClick(R.id.letShare)
//    public void letShare() {
//        Toast.makeText(this, "Hi, If you like the experience, Please let your friends know about us. Thank you", Toast.LENGTH_LONG).show();
//    }

    @Override
    protected void onResume() {
        super.onResume();

        mViatoApp.sendScreenView(getString(R.string.booking_detail_fragment));
//        Analytics.with(this).screen("screen", getString(R.string.booking_detail_fragment));
    }
}
