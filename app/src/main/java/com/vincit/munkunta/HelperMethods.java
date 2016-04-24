package com.vincit.munkunta;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;

import org.apache.commons.validator.routines.UrlValidator;

public class HelperMethods extends Application {

    /**
     * Converts dps to pixels
     * @param dps
     * @return pixels
     */

    public static int toPx(Double dps) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    /**
     * Converts base64 string to Bitmap
     * @param base64
     * @return Bitmap
     */

    public static Bitmap toBitmap(String base64) {
        byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        return image;
    }



    /**
     * Checks whether the url is valid or not
     * @param string
     * @return boolean
     */

    public static boolean validURL(String string) {
        UrlValidator urlValidator = new UrlValidator();
        boolean isURL = urlValidator.isValid(string);
        return isURL;

    }

    public static void changeStatusBarColor(Activity a, Municipality activeMuni) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = a.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(activeMuni.getSColor()));
        }
    }

}
