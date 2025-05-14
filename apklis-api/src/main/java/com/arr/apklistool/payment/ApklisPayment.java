package com.arr.apklistool.payment;

import android.content.ContentProviderClient;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class ApklisPayment {

    private static final String TAG = "ApklisPay";
    private static final String APKLIS_URL = "content://cu.uci.android.apklis.payment.provider/app/";
    private static final String APK_PAID = "paid";
    private static final String USER = "user_name";

    private final Context mContext;
    private final String packageName;

    private boolean isPaid = false;
    private String usuario = null;

    public ApklisPayment(Context context, String packageName) {
        this.mContext = context;
        this.packageName = packageName;
        checkPayment();
    }

    private void checkPayment() {
        Uri provider = Uri.parse(APKLIS_URL + packageName);

        ContentProviderClient content = null;
        try {
            content = mContext.getContentResolver().acquireContentProviderClient(provider);
            if (content != null) {
                Cursor cursor = content.query(provider, null, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int paidColumnIndex = cursor.getColumnIndex(APK_PAID);
                        int userColumnIndex = cursor.getColumnIndex(USER);
                        isPaid = cursor.getInt(paidColumnIndex) > 0;
                        usuario = cursor.getString(userColumnIndex);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        content.close();
                    } else {
                        content.release();
                    }
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "checkPayment: Fail", e);
            ;
        } finally {
            if (content != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                content.release();
            }
        }
    }

    public boolean isPaid() {
        return isPaid;
    }

    public String userName() {
        return usuario;
    }
}
        /*
            private static final String TAG = "ApklisPayment";
            private static final String APKLIS_URL =
                    "content://cu.uci.android.apklis.payment.provider/app/";
            private static final String APK_PAID = "paid";
            private static final String USER = "user_name";

            private static boolean isPaid = false;
            private static String usuario = null;

            public static boolean isPaidApp(Context context) {
                Uri provider = Uri.parse(APKLIS_URL + context.getPackageName());
                ContentProviderClient content = null;
                Cursor cursor = null;

                try {
                    content = context.getContentResolver().acquireContentProviderClient(provider);
                    if (content != null) {
                        cursor = content.query(provider, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            int paidColumnIndex = cursor.getColumnIndex(APK_PAID);
                            int userColumnIndex = cursor.getColumnIndex(USER);

                            if (paidColumnIndex != -1) {
                                isPaid = cursor.getInt(paidColumnIndex) > 0;
                            }

                            if (userColumnIndex != -1) {
                                usuario = cursor.getString(userColumnIndex);
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "checkPayment: Error while checking payment", e);
                } finally {
                    if (cursor != null) {
                        cursor.close(); // Asegurarse de cerrar el cursor
                    }
                    if (content != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            content.close();
                        } else {
                            content.release();
                        }
                    }
                }

                return isPaid;
            }
        }
        */
