package in.viato.app.ui.fragments;


import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.Bind;
import in.viato.app.R;
import in.viato.app.http.models.response.SimpleResponse;
import in.viato.app.ui.widgets.CirclePageIndicator;
import rx.functions.Action1;

/**
 * Created by ranadeep on 15/09/15.
 */
public class LoginFragment extends AbstractFragment implements ViewPager.OnPageChangeListener{

    public static final String TAG = "LoginFragment";
    private static final long SWITCH_INTERVAL = 4000L;

    private PagerAdapter mPagerAdapter;

    private int indexSwitchImage = 0;
    private String[] mImagesTitles;
    private String[] mImagesSubTitles;
    private final int[] mImagesArray = new int[] {R.drawable.one, R.drawable.two, R.drawable.three};

    private CountDownTimer mImageSlideTimer;
    private boolean mReverse;

    @Bind(R.id.imagePager) ViewPager mImagePager;
    @Bind(R.id.imagePagerIndicator) CirclePageIndicator mCirclePageIndicator;
    @Bind(R.id.mobile_input_label) TextInputLayout mMobileInputLabel;
    @Bind(R.id.mobile_input) EditText mMobileInput;
    @Bind(R.id.submit) Button mSubmit;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mImagesTitles = getActivity().getResources().getStringArray(R.array.title_array);
        mImagesSubTitles = getActivity().getResources().getStringArray(R.array.subtitle_array);
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ButterKnife bind finishes here

        mPagerAdapter = new PagerAdapter(getChildFragmentManager());
        mImagePager.setAdapter(mPagerAdapter);
        mCirclePageIndicator.setOnPageChangeListener(this);
        mCirclePageIndicator.setViewPager(mImagePager);

        startSlideShow();
        mImagePager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mImageSlideTimer.cancel();
                return false;
            }
        });

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
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageSlideTimer.cancel();
    }

    private void validateAndSubmit(){
        //Check if the phone number has 10 chars
        String mobile_number = mMobileInput.getText().toString();
        if(mobile_number.length() != getActivity().getResources().getInteger(R.integer.phone_number_length)){
            //Show error
            mMobileInputLabel.setError(getString(R.string.error_mobile_input));
            mMobileInputLabel.setErrorEnabled(true);
        }
        else {
            mMobileInputLabel.setErrorEnabled(false);
            //Got a valid mobile, send to server
            mViatoAPI
                    .login(mobile_number)
                    .subscribe(new Action1<SimpleResponse>() {
                        @Override
                        public void call(SimpleResponse simpleResponse) {
                            if(simpleResponse.isSuccess()){
                                hideKeyboard(mMobileInput);
                                loadFragment(R.id.frame_content,
                                        LoginConfirmFragment.newInstance(),
                                        LoginConfirmFragment.TAG,
                                        true,
                                        LoginConfirmFragment.TAG
                                );
                            }
                            else {
                                String error = simpleResponse.getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }


    }

    private void startSlideShow() {

        final int numberOfItems = mImagesArray.length;
        mImageSlideTimer = new CountDownTimer((numberOfItems + 1) * SWITCH_INTERVAL, SWITCH_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                mImagePager.setCurrentItem(indexSwitchImage, true);

                if (indexSwitchImage == 0) {
                    mImagePager.setCurrentItem(0, true);
                    indexSwitchImage = 1;
                    mReverse = false;
                } else if (indexSwitchImage == 1) {
                    if (mReverse) {
                        indexSwitchImage = 0;
                    } else {
                        indexSwitchImage = 2;
                    }
                    mImagePager.setCurrentItem(1, true);

                } else if (indexSwitchImage == 2) {
                    mImagePager.setCurrentItem(2, true);
                    indexSwitchImage = 1;
                    mReverse = true;
                }
            }

            @Override
            public void onFinish() {
                start();
            }
        };
        mImageSlideTimer.start();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public boolean onBackPressed(){
        //Handle backpress but do nothing !. I mean its the first fragment !
        return true;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {

        public PagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return ImageViewFragment.newInstance(
                    mImagesArray[i],
                    mImagesTitles[i],
                    mImagesSubTitles[i]
            );
        }

        @Override
        public int getCount() {
            return mImagesArray.length;
        }
    }

}

//TODO store the mobile number in shared prefs