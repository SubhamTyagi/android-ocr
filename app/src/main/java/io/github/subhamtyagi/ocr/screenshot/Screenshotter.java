package io.github.subhamtyagi.ocr.screenshot;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.util.Log;

import java.nio.Buffer;

import static android.graphics.PixelFormat.RGBA_8888;


@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Screenshotter implements ImageReader.OnImageAvailableListener {

    private static final String TAG = "Screenshotter";
    private VirtualDisplay virtualDisplay;
    private int width;
    private int height;
    private ScreenshotCallback cb;
    private ImageReader mImageReader;
    private MediaProjection mMediaProjection;

    public Screenshotter(MediaProjection mediaProjection) {
        mMediaProjection = mediaProjection;
    }

    /**
     * Take screenshot.
     *
     * @param cb the cb
     */
    @SuppressLint("WrongConstant")
    public void takeScreenshot(final ScreenshotCallback cb) {
        this.cb = cb;
        mImageReader = ImageReader.newInstance(width, height, RGBA_8888, 1);
        if (mMediaProjection != null) {
            try {
                virtualDisplay = mMediaProjection.createVirtualDisplay("Screenshotter",
                        width, height, 50,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        mImageReader.getSurface(), null, null);
                mImageReader.setOnImageAvailableListener(Screenshotter.this, null);
            } catch (Exception e) {
                //do if something goes wrong
            }
        }

    }

    /**
     * Set the size of the screenshot to be taken
     *
     * @param width  width of the requested bitmap
     * @param height height of the request bitmap
     * @return the singleton instance
     */
    public Screenshotter setSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        Image image = reader.acquireLatestImage();
        if (image == null) {
            Log.d(TAG, "onImageAvailable: image is null");
            return;
        }

        final Image.Plane[] planes = image.getPlanes();
        final Buffer buffer = planes[0].getBuffer().rewind();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        tearDown();
        image.close();
        cb.onScreenshot(bitmap);
    }

    private void tearDown() {
        virtualDisplay.release();
        if (mMediaProjection != null) mMediaProjection.stop();
        mMediaProjection = null;
        mImageReader = null;
    }

    /**
     * The interface Screenshot callback.
     */
    public interface ScreenshotCallback {
        void onScreenshot(Bitmap bitmap);
    }
}
