package in.viato.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;

import in.viato.app.ViatoApplication;

/**
 * Created by ranadeep on 12/09/15.
 */
public class MiscUtils {

    private static final int MIN_DISK_CACHE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final int MAX_DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    public static float distanceBetween(final Location start, final Location end) {
        final float[] results = new float[1];
        Location.distanceBetween(start.getLatitude(), start.getLongitude(), end
                .getLatitude(), end.getLongitude(), results);
        return results[0];
    }

    public static File getStorageDirectory(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return context.getExternalFilesDir(null);
        else
            return context.getFilesDir();
    }

    public static boolean copyFile(final File src, final File dst) {
        String TAG = "MiscUtils:copyFile";
        boolean returnValue = true;

        FileChannel inChannel = null, outChannel = null;

        try {

            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();

        } catch (final FileNotFoundException fnfe) {

            Logger.d(TAG, "inChannel/outChannel FileNotFoundException");
            fnfe.printStackTrace();
            return false;
        }

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (final IllegalArgumentException iae) {

            Logger.d(TAG, "TransferTo IllegalArgumentException");
            iae.printStackTrace();
            returnValue = false;

        } catch (final NonReadableChannelException nrce) {

            Logger.d(TAG, "TransferTo NonReadableChannelException");
            nrce.printStackTrace();
            returnValue = false;

        } catch (final NonWritableChannelException nwce) {

            Logger.d(TAG, "TransferTo NonWritableChannelException");
            nwce.printStackTrace();
            returnValue = false;

        } catch (final ClosedByInterruptException cie) {

            Logger.d(TAG, "TransferTo ClosedByInterruptException");
            cie.printStackTrace();
            returnValue = false;

        } catch (final AsynchronousCloseException ace) {

            Logger.d(TAG, "TransferTo AsynchronousCloseException");
            ace.printStackTrace();
            returnValue = false;

        } catch (final ClosedChannelException cce) {

            Logger.d(TAG, "TransferTo ClosedChannelException");
            cce.printStackTrace();
            returnValue = false;

        } catch (final IOException ioe) {

            Logger.d(TAG, "TransferTo IOException");
            ioe.printStackTrace();
            returnValue = false;

        } finally {

            if (inChannel != null) {
                try {

                    inChannel.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

            if (outChannel != null) {
                try {
                    outChannel.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return returnValue;
    }

    public static PackageInfo getPackageInfo() {
        PackageInfo info;
        try {
            info = ViatoApplication.get().getPackageManager()
                    .getPackageInfo(ViatoApplication.get().getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            //Shouldn't happen
            info = null;
        }
        return info;
    }

    public static String getDeviceId(){
        return Settings.Secure.getString(ViatoApplication.get().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static boolean isNetConnected(Context context){
        return ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo()
                .isConnectedOrConnecting();

    }

    public static long calculateDiskCacheSize(File dir) {
        long size = MIN_DISK_CACHE_SIZE;

        try {
            StatFs statFs = new StatFs(dir.getAbsolutePath());
            long available =  ((long) statFs.getBlockCount()) * statFs.getBlockSize();
            // Target 2% of the total space.
            size = available / 50;
        } catch (IllegalArgumentException ignored) {
        }

        // Bound inside min/max size for disk cache.
        return Math.max(Math.min(size, MAX_DISK_CACHE_SIZE), MIN_DISK_CACHE_SIZE);
    }
}
