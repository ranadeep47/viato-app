package in.viato.app.ui.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.http.clients.login.HttpClient;
import in.viato.app.ui.widgets.CirclePageIndicator;
import in.viato.app.utils.AppConstants;
import in.viato.app.utils.SharedPrefHelper;
import in.viato.app.utils.ui.ViewUtils;
import retrofit.HttpException;
import retrofit.Response;
import rx.Subscriber;

/**
 * Created by ranadeep on 15/09/15.
 */
public class LoginFragment extends AbstractFragment {
//   implements ViewPager.OnPageChangeListener

    public static final String TAG = LoginFragment.class.getSimpleName();

    private final HttpClient mHttpClient = ViatoApplication.get().getHttpClient();

    private String device_id;

//    private static final long SWITCH_INTERVAL = 4000L;
//    private PagerAdapter mPagerAdapter;
//    private int indexSwitchImage = 0;
//    private String[] mImagesTitles;
//    private String[] mImagesSubTitles;
//    private final int[] mImagesArray = new int[] {R.drawable.one, R.drawable.two, R.drawable.three};
//    private CountDownTimer mImageSlideTimer;
//    private boolean mReverse;

//    @Bind(R.id.imagePager) ViewPager mImagePager;
//    @Bind(R.id.imagePagerIndicator) CirclePageIndicator mCirclePageIndicator;

    @Bind(R.id.mobile_input_label) TextInputLayout mMobileInputLabel;
    @Bind(R.id.mobile_input) EditText mMobileInput;
    @Bind(R.id.submit) Button mSubmit;
    @Bind(R.id.progress_bar) ProgressBar mProgress;
    @Bind(R.id.banner) ImageView mBanner;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

//        mImagesTitles = getActivity().getResources().getStringArray(R.array.title_array);
//        mImagesSubTitles = getActivity().getResources().getStringArray(R.array.subtitle_array);

        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife bind finishes here

//        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
//        mImagePager.setAdapter(mPagerAdapter);
//        mCirclePageIndicator.setOnPageChangeListener(this);
//        mCirclePageIndicator.setViewPager(mImagePager);
//
//        startSlideShow();
//        mImagePager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                mImageSlideTimer.cancel();
//                return false;
//            }
//        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int)ViewUtils.convertPixelsToDp(size.x, getContext());
        int height = (int)ViewUtils.convertPixelsToDp(size.y, getContext());
        Logger.d("height " + width + " " + height);
        mBanner.setImageBitmap(
                decodeSampledBitmapFromResource(getResources(), R.drawable.intro_cover, width, height));

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndSubmit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViatoApplication.get().trackScreenView(getString(R.string.login_fragment));
//        Analytics.with(getContext()).screen("screen", getString(R.string.login_fragment));
    }

    @Override
    public void onPause() {
        super.onPause();
//        mImageSlideTimer.cancel();
    }

    private void validateAndSubmit(){
        //Check if the phone number has 10 chars
        String mobile_number = mMobileInput.getText().toString();
        if(!mobile_number.matches(getString(R.string.regex_mobile_number))){
            //Show error
            mMobileInputLabel.setError(getString(R.string.error_mobile_input));
            mMobileInputLabel.setErrorEnabled(true);
        } else {
            mMobileInputLabel.setErrorEnabled(false);

            //save phone number and device id in shared preferences
            SharedPrefHelper.set(R.string.pref_mobile_number, mobile_number);
            SharedPrefHelper.set(R.string.pref_device_id, device_id);

            AppConstants.UserInfo.INSTANCE.setMobileNumber(mobile_number);
            AppConstants.UserInfo.INSTANCE.setDeviceId(device_id);

            mSubmit.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);

            mHttpClient.login(mobile_number, device_id)
                    .subscribe(new Subscriber<Response<String>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            Logger.e(e.getMessage());
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            mSubmit.setVisibility(View.VISIBLE);
                            mProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onNext(Response<String> stringResult) {
                            if (stringResult.isSuccess()) {
                                mViatoApp.trackEvent(getString(R.string.login_fragment), "login", "submit", "phone");
                                Toast.makeText(getContext(), stringResult.body(), Toast.LENGTH_SHORT).show();
                                hideKeyboard(mMobileInput);
                                loadFragment(R.id.frame_content,
                                        LoginConfirmFragment.newInstance(),
                                        LoginConfirmFragment.TAG,
                                        true,
                                        LoginConfirmFragment.TAG
                                );
                            } else {
                                try {
                                    Logger.e(stringResult.errorBody().string());
                                    Toast.makeText(getContext(), stringResult.errorBody().string(), Toast.LENGTH_LONG).show();
                                } catch (IOException e) {
                                    Logger.e(e, "error");
                                }
                            }
                        }
                    });
        }
    }

    public boolean onBackPressed(){
        //Handle backpress but do nothing !. I mean its the first fragment !
        return true;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //    private void startSlideShow() {
//
//        final int numberOfItems = mImagesArray.length;
//        mImageSlideTimer = new CountDownTimer((numberOfItems + 1) * SWITCH_INTERVAL, SWITCH_INTERVAL) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                mImagePager.setCurrentItem(indexSwitchImage, true);
//
//                if (indexSwitchImage == 0) {
//                    mImagePager.setCurrentItem(0, true);
//                    indexSwitchImage = 1;
//                    mReverse = false;
//                } else if (indexSwitchImage == 1) {
//                    if (mReverse) {
//                        indexSwitchImage = 0;
//                    } else {
//                        indexSwitchImage = 2;
//                    }
//                    mImagePager.setCurrentItem(1, true);
//
//                } else if (indexSwitchImage == 2) {
//                    mImagePager.setCurrentItem(2, true);
//                    indexSwitchImage = 1;
//                    mReverse = true;
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                start();
//            }
//        };
//        mImageSlideTimer.start();
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//
//    }
//
//    @Override
//    public void onPageScrollStateChanged(int state) {
//
//    }
//
//    public class PagerAdapter extends FragmentStatePagerAdapter {
//
//        public PagerAdapter(final FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int i) {
//            return ImageViewFragment.newInstance(
//                    mImagesArray[i],
//                    mImagesTitles[i],
//                    mImagesSubTitles[i]
//            );
//        }
//
//        @Override
//        public int getCount() {
//            return mImagesArray.length;
//        }
//    }

}