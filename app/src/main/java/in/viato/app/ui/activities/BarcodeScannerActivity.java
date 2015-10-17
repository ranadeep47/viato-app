package in.viato.app.ui.activities;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.FocusingProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import in.viato.app.R;
import in.viato.app.ViatoApplication;
import in.viato.app.utils.barcode.CameraSourcePreview;

/**
 * Created by ranadeep on 22/09/15.
 */
public class BarcodeScannerActivity extends AppCompatActivity {
    private static final String TAG = "BarcodeScanner";
    private static final int RC_HANDLE_GMS = 9001;

    private CameraSource mCameraSource = null;
    private Tracker<Barcode> mTracker;
    private FocusingProcessor<Barcode> mProcessor;
    private int mTrackingId = 0;

    private Handler mHandler;

    private CameraSourcePreview mPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        mHandler = new Handler();
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        createCameraSource();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }

        mProcessor.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {


        Context context = ViatoApplication.get();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        BarcodeDetector detector = new BarcodeDetector.Builder(context)
                .setBarcodeFormats(Barcode.EAN_13 | Barcode.UPC_A)
                .build();

        if (!detector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low storage. Unable to download libraries to scan barcode", Toast.LENGTH_LONG).show();
            }

            return;
        }

        mTracker = new Tracker<Barcode>(){
            @Override
            public void onNewItem(int id, Barcode item) {
                String message;
                int len = item.rawValue.length();
                if(len == 10 || len == 13 ){
                    String isbn;
                    mTrackingId = id;
                    if(len == 13){
                        if(!(validateISBN(item.rawValue))){
                            message = "Barcode is not that of a book.";
                            showErrorMessage(message);
                            return;
                        }
                    }
                    isbn = item.rawValue;
                    Logger.d("ISBN code : %s", item.rawValue);
                    sendISBN(isbn);
                }
                else if (len == 12){
                    message = "You seem to have a very old book. Please use search instead";
                    showErrorMessage(message);
                }
                else {
                    message = "Strange !";
                    showErrorMessage(message);
                    Logger.d("I shouldn't ever print !");
                }

            }

            public void showErrorMessage(final String message){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ViatoApplication.get(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            private boolean validateISBN(String isbn){
                String SEP = "(?:\\-|\\s)";
                String GROUP = "(\\d{1,5})";
                String PUBLISHER = "(\\d{1,7})";
                String TITLE = "(\\d{1,6})";
                String ISBN10_REGEX = "^(?:(\\d{9}[0-9X])|(?:" + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9X])))$";
                String ISBN13_REGEX = "^(978|979)(?:(\\d{10})|(?:" + SEP + GROUP + SEP + PUBLISHER + SEP + TITLE + SEP + "([0-9])))$";

                return (isbn.matches(ISBN10_REGEX) || isbn.matches(ISBN13_REGEX));
            }
        };

        mProcessor = new FocusingProcessor<Barcode>(detector, mTracker) {
            @Override
            public int selectFocus(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(detections.detectorIsOperational()){
                    return barcodes.keyAt(0);
                }
                return mTrackingId;
            }
        };

        detector.setProcessor(mProcessor);

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        mCameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f)
                .build();
    }


    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void sendISBN(String isbn){
        if (getCallingActivity() == null) {
            String query = isbn;
            Intent intent = new Intent(getApplicationContext(), BookSearchActivity.class);
            intent.putExtra(SearchManager.QUERY, query);
            intent.setAction("android.intent.action.SEARCH");
            startActivity(intent);
        } else{
            Intent data = new Intent();
            data.putExtra("isbn", isbn);
            setResult(RESULT_OK, data);
        }
        finish();
    }

}
