package in.viato.app.ui.activities;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.FrameLayout;

import in.viato.app.R;

public class SuccessActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        FrameLayout view =  (FrameLayout) findViewById(R.id.frame_content);
        NestedScrollView view1 = (NestedScrollView) getLayoutInflater().inflate(R.layout.activity_success, null);
        view.addView(view1);
    }
}
