package com.arr.simple.helpers.ussd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class USSDUtils {

    private Context context;

    public USSDUtils(Context context) {
        this.context = context;
    }

    public void executeUSSD(String ussd) {
        try {
            this.context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussd)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
