package in.viato.app.ui.widgets;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

/**
 * Created by saiteja on 25/09/15.
 */
public class NoScrollRecyclerView extends RecyclerView {
    private static final String TAG = "NoScrollRecyclerView";

    public NoScrollRecyclerView(Context context){
        super(context);
    }

    public NoScrollRecyclerView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public NoScrollRecyclerView(Context context, AttributeSet attrs, int style){
        super(context, attrs, style);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        //Ignore scroll events.
        if(ev.getAction() == MotionEvent.AXIS_VSCROLL) {
            return true;
        }

        //Dispatch event for non-scroll actions, namely clicks!
        return super.dispatchTouchEvent(ev);
    }
}