package com.arr.apklistool.payment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.content.ContentProviderClient;
import android.util.Log;

public class AppPurchase {

    private static final String TAG = "AppPurchase";
    private static final String APKLIS_PROVIDER =
            "content://cu.uci.android.apklis.PaymentProvider/app/";
    private static final String APKLIS_PAID = "paid";

    public static boolean isPurchase(Context context) {
        if (context == null) {
            Log.e(TAG, "Context is null");
            return false;
        }

        String packageName = context.getPackageName();
        Uri providerURI = Uri.parse(APKLIS_PROVIDER + packageName);
        ContentProviderClient contentResolver = null;
        Cursor cursor = null;

        try {
            contentResolver =
                    context.getContentResolver().acquireContentProviderClient(providerURI);
            if (contentResolver == null) {
                Log.e(TAG, "ContentProvider not found for: " + providerURI);
                return false;
            }

            cursor = contentResolver.query(providerURI, null, null, null, null);
            if (cursor == null) {
                Log.e(TAG, "Cursor is null");
                return false;
            }

            if (cursor.moveToFirst()) {
                int paidColumnIndex = cursor.getColumnIndex(APKLIS_PAID);
                if (paidColumnIndex != -1) {
                    return cursor.getInt(paidColumnIndex) > 0;
                } else {
                    Log.e(TAG, "Column 'paid' not found");
                }
            } else {
                Log.e(TAG, "Cursor is empty");
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException: " + e.getMessage(), e);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "IllegalArgumentException: " + e.getMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing cursor", e);
                }
            }
            if (contentResolver != null) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contentResolver.close();
                    } else {
                        contentResolver.release();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error closing ContentProviderClient", e);
                }
            }
        }

        return false;
    }
}
