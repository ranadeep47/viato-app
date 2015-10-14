package in.viato.app.ui.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import in.viato.app.R;
import in.viato.app.ui.widgets.BetterViewAnimator;
import rx.Observable;
import in.viato.app.ui.widgets.CustomAlertDialogBuilder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ModifyOrderFragment extends DialogFragment {
    private static final String ARG_ACTION = "action";
    private static final String ARG_ID = "id";

    public static final int ACTION_EXTEND = 0;
    public static final int ACTION_RETURN = 1;

    CustomAlertDialogBuilder dialog;

    private int mAction;
    private String mId;

    Observable observable;

    @Bind(R.id.container_animator) BetterViewAnimator mContainer;
    @Bind(R.id.progress_bar) LinearLayout mBar;
    @Bind(R.id.confirm_return) RelativeLayout mConfirmReturn;
    @Bind(R.id.confirm_extend) RelativeLayout mConfirmExtend;
    @Bind(R.id.success) LinearLayout mSuccess;

    public static ModifyOrderFragment newInstance(int action, String id) {
        ModifyOrderFragment fragment = new ModifyOrderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ACTION, action);
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public ModifyOrderFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_modify_order, null);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            mAction = getArguments().getInt(ARG_ACTION);
            mId = getArguments().getString(ARG_ID);
        }

        String title = "";

        switch(mAction) {
            case ACTION_EXTEND:
                title = "Want to extend return period?";
                mContainer.setDisplayedChildView(mConfirmExtend);
                break;
            case ACTION_RETURN:
                title = "Want to return the book?";
                mContainer.setDisplayedChildView(mConfirmExtend);
                break;
            default:
                break;
        }


        return new CustomAlertDialogBuilder(getActivity())
                .setView(view)
                .setTitle(title)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        mContainer.setDisplayedChildView(mBar);
                        Observable.timer(5000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber() {
                                    @Override
                                    public void onCompleted() {
                                        mContainer.setDisplayedChildView(mSuccess);
//                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onError(Throwable e) {

                                    }

                                    @Override
                                    public void onNext(Object o) {

                                    }
                                });
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

//    private void sendResult(int resultCode, float rating, String review) {
//        if (getTargetFragment() == null) {
//            return;
//        }
//
//        Intent intent = new Intent();
//        intent.putExtra(EXTRA_RATING, rating);
//        intent.putExtra(EXTRA_REVIEW, review);
//
//        getTargetFragment()
//                .onActivityResult(getTargetRequestCode(), resultCode, intent);
//    }
}
