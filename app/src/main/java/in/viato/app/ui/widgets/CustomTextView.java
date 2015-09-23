package in.viato.app.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import in.viato.app.R;
import in.viato.app.ViatoApplication;

/**
 * Created by saiteja on 22/09/15.
 */
public class CustomTextView extends TextView {
    private final static int ROBOTO = 0;
    private final static int ROBOTO_CONDENSED = 1;

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttributes(context, attrs);
    }

    private void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        //The value 0 is a default, but shouldn't ever be used since the attr is an enum
        int typeface = values.getInt(R.styleable.CustomTextView_typeface, 0);

        switch(typeface) {
            case ROBOTO: default:
                //You can instantiate your typeface anywhere, I would suggest as a
                //singleton somewhere to avoid unnecessary copies
                setTypeface(ViatoApplication.roboto);
                break;
            case ROBOTO_CONDENSED:
                setTypeface(ViatoApplication.robotoCondensed);
                break;
        }

        values.recycle();
    }
}
