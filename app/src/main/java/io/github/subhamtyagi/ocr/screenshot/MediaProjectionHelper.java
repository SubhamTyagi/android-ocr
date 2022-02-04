package ;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;

/**
 * The type Media projection helper.
 */
public class MediaProjectionHelper {

    /**
     * The constant screenshotPermission.
     */
    public static Intent screenshotPermission;
    //private static int resultCodeForPermission;
    private static MediaProjectionManager mediaProjectionManager;
    private static MediaProjection mMediaProjection;


    /**
     * Sets media projection manager.
     *
     * @param mediaProjectionManager the media projection manager
     */
    public static void setMediaProjectionManager(MediaProjectionManager mediaProjectionManager) {
        MediaProjectionHelper.mediaProjectionManager = mediaProjectionManager;
    }

    /**
     * Sets screenshot permission.
     *
     * @param screenshotPermission the screenshot permission
     */
    public static void setScreenshotPermission(Intent screenshotPermission) {
        MediaProjectionHelper.screenshotPermission = screenshotPermission;
    }

    /**
     * Get media projection media projection.
     *
     * @param context the context
     * @return the media projection
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected static MediaProjection getMediaProjection(Context context){
        MediaProjectionManager mgr= (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if(mMediaProjection!=null){
            mMediaProjection.stop();
            mMediaProjection=null;
        }
        mMediaProjection = mgr.getMediaProjection(Activity.RESULT_OK, (Intent) screenshotPermission.clone());
        return mMediaProjection;
    }

}
