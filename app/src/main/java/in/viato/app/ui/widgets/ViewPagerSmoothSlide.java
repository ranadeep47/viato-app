package in.viato.app.ui.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

/**
 * Created by ranadeep on 14/09/15.
 */
public class ViewPagerSmoothSlide extends ViewPager {
    public ViewPagerSmoothSlide( Context context, AttributeSet attrs)
    {
        super( context, attrs );
        setMyScroller();
    }
    private void setMyScroller()
    {
        try
        {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e)
        {
            Logger.e(e, "error");
        }
    }

    public class MyScroller extends Scroller
    {
        public MyScroller(Context context)
        {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration)
        {
            super.startScroll(startX, startY, dx, dy, 500 /*.5 secs*/);
        }
    }
}
