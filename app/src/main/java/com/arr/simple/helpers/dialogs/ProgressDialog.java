package com.arr.simple.helpers.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import com.arr.simple.R;
import android.view.WindowManager;
import com.arr.simple.databinding.LayoutViewProgressBinding;

public class ProgressDialog extends Dialog {

    private LayoutViewProgressBinding binding;

    public ProgressDialog(Context context) {
        super(context);
        binding = LayoutViewProgressBinding.inflate(getLayoutInflater());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        getWindow().setAttributes(params);
        getWindow()
                .setBackgroundDrawable(
                        getContext().getDrawable(R.drawable.background_progress_dialog));
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(binding.getRoot());
    }
}
