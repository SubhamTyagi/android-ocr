package io.github.subhamtyagi.ocr.utils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CrashUtils implements Thread.UncaughtExceptionHandler {

    private static final String EXCEPTION_SUFFIX = "_exception";
    private static final String CRASH_SUFFIX = "_crash";
    private static final String FILE_EXTENSION = ".txt";
    private static final String CRASH_REPORT_DIR = "crashReports";
    private static final String TAG = "CrashUtils";
    private final Thread.UncaughtExceptionHandler exceptionHandler;
    private final Context applicationContext;
    private final String crashReportPath;

    public CrashUtils(Context context, String crashReportSavePath) {
        this.exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        applicationContext = context;
        crashReportPath = crashReportSavePath;
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CrashUtils)) {
            Thread.setDefaultUncaughtExceptionHandler(this);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        saveCrashReport(throwable);
        exceptionHandler.uncaughtException(thread, throwable);
    }

    private void saveCrashReport(final Throwable throwable) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String filename = dateFormat.format(new Date()) + CRASH_SUFFIX + FILE_EXTENSION;
        writeToFile(crashReportPath, filename, getStackTrace(throwable));
    }

    private void writeToFile(String crashReportPath, String filename, String crashLog) {
        if (TextUtils.isEmpty(crashReportPath)) {
            crashReportPath = getDefaultPath();
        }
        File crashDir = new File(crashReportPath);
        if (!crashDir.exists() || !crashDir.isDirectory()) {
            crashReportPath = getDefaultPath();
            Log.e(TAG, "Path provided doesn't exists : " + crashDir + "\nSaving crash report at : " + getDefaultPath());
        }
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(
                    new FileWriter(crashReportPath + File.separator + filename));
            bufferedWriter.write(crashLog);
            bufferedWriter.flush();
            bufferedWriter.close();
            Log.d(TAG, "crash report saved in : " + crashReportPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getStackTrace(Throwable e) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String crashLog = result.toString();
        printWriter.close();
        return crashLog;
    }

    private String getDefaultPath() {
        String defaultPath = applicationContext.getExternalFilesDir(null).getAbsolutePath()
                + File.separator + CRASH_REPORT_DIR;
        File file = new File(defaultPath);
        file.mkdirs();
        return defaultPath;
    }

    public void logException(final Exception exception) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                final String filename = dateFormat.format(new Date()) + EXCEPTION_SUFFIX + FILE_EXTENSION;
                writeToFile(crashReportPath, filename, getStackTrace(exception));
            }
        }).start();
    }

}
