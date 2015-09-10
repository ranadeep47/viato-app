package in.viato.app.ui.activties;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import in.viato.app.R;

public class MainActivity extends AbstractNavDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToolbar.setTitle(R.string.app_name);
        mToolbar.setSubtitle(R.string.app_description);
        mToolbar.setTitleTextAppearance(this, R.style.Viato_ActionBar_Title);
        mToolbar.setSubtitleTextAppearance(this, R.style.Viato_ActionBar_Subtitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
