package com.fee.sharemaster;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * *****************(^_^)***********************<br>
 * Author: fee<br>
 * <P>DESC:
 *
 * </p>
 * ******************(^_^)***********************
 */
public class MyFileProvider extends FileProvider {
    public static final String AUTHORITIES = ".MyFileProvider";

    public static Uri getUri(Context context, File file) {
        if (context == null || file == null)
            return null;

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//Android 7.0
            uri = getUriForFile(context, context.getPackageName() + AUTHORITIES, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static Uri getUri(Context context, String filePath) {
        if (context == null || TextUtils.isEmpty(filePath))
            return null;
        try {
            File file = new File(filePath);
            return getUri(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
