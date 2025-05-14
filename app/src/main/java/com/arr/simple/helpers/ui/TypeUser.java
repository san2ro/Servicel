package com.arr.simple.helpers.ui;

import android.text.InputFilter;
import android.text.InputType;
import com.arr.simple.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class TypeUser {

    public static void typeMobileUsername(TextInputLayout layout, TextInputEditText editText) {
        layout.setEndIconDrawable(R.drawable.ic_email_24dp);
        editText.setInputType(InputType.TYPE_CLASS_PHONE);
        layout.setPrefixText("(+53)");
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(8)});
        layout.setSuffixText(null);
        editText.getText().clear();
    }

    public static void typeMailUsername(TextInputLayout layout, TextInputEditText editText) {
        layout.setEndIconDrawable(R.drawable.ic_mobile_24dp);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(60)});
        layout.setPrefixText(null);
        layout.setSuffixText("@nauta.cu");
        editText.getText().clear();
    }
}
