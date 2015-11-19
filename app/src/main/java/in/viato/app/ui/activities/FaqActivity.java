package in.viato.app.ui.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import butterknife.Bind;
import in.viato.app.R;

public class FaqActivity extends AbstractNavDrawerActivity {

    @Bind(R.id.frame_content) FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_faq, null);
        mFrameLayout.addView(view);

        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.loadUrl(getResources().getString(R.string.base_url) + "faq/");
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return getResources().getInteger(R.integer.nav_item_faq);
    }
}
