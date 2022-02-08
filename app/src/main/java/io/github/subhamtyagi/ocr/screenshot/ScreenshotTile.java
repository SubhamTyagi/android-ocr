package io.github.subhamtyagi.ocr.screenshot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;

import io.github.subhamtyagi.ocr.MainActivity;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ScreenshotTile extends TileService {
    private static final int REQUEST_MEDIA_PROJECTION = 155;
    private MediaProjection mMediaProjection;

    public ScreenshotTile() {
    }

    @Override
    public void onClick() {
        super.onClick();
        requestScreenshotPermission();
        handleScreenshot();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("screenshot");
        startActivity(intent);

    }


    private void requestScreenshotPermission() {
        MediaProjectionManager mgr = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        //startActivity(mgr.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        mMediaProjection = mgr.getMediaProjection(Activity.RESULT_OK, null);
    }

    private void handleScreenshot() {
        Screenshotter screenshotter = new Screenshotter(mMediaProjection);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenshotter.setSize(dm.widthPixels, dm.heightPixels);
        screenshotter.takeScreenshot(bitmap -> {
            try {
                String filename = "screenshot.png";
                FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                bitmap.recycle();
                //
                Uri uri = Uri.fromFile(new File("screenshot.png"));


            } catch (Exception ignore) {

            }
        });


    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }


}
