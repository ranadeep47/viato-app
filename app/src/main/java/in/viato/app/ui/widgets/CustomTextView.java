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
    private final static int ROBOTO_THIN = 1;
    private final static int ROBOTO_MEDIUM = 2;
    private final static int ROBOTO_REGULAR = 3;

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
//        TypedArray values = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        //The value 0 is a default, but shouldn't ever be used since the attr is an enum
//        int typeface = values.getInt(R.styleable.CustomTextView_typeface, 0);

//        switch(typeface) {
//            //You can instantiate your typeface anywhere, I would suggest as a
//            //singleton somewhere to avoid unnecessary copies
//            case ROBOTO: default:
//                setTypeface(ViatoApplication.roboto);
//                break;
//            case ROBOTO_THIN:
//                setTypeface(ViatoApplication.robotoThin);
//                break;
//            case ROBOTO_MEDIUM:
//                setTypeface(ViatoApplication.robotoMedium);
//                break;
//            case ROBOTO_REGULAR:
//                setTypeface(ViatoApplication.robotoRegular);
//                break;
//        }

//        values.recycle();
    }
}
