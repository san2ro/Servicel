package com.arr.simple.activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.arr.simple.BaseActivity;
import com.arr.simple.R;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import com.arr.simple.MainActivity;
import com.arr.simple.databinding.ActivityDonationBinding;
import com.arr.simple.helpers.dialogs.DonationDialog;

public class DonationActivity extends BaseActivity {

    private ActivityDonationBinding binding;

    private boolean donateMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        goBackPressed();

        binding.chipGroup.setOnCheckedStateChangeListener(
                (group, checkedIds) -> {
                    if (!checkedIds.isEmpty()) {
                        int selectedId = checkedIds.get(0);

                        if (selectedId == R.id.transfer) {
                            donateMethod = true;
                        } else if (selectedId == R.id.saldo) {
                            donateMethod = false;
                        }
                    }
                });

        binding.ok.setOnClickListener(this::donate);
    }

    private void donate(View view) {
        String monto = binding.amount.getText().toString().replace("$", "");
        if (!TextUtils.isEmpty(monto)) {
            if (donateMethod) {
                new DonationDialog(this).show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("MONTO", monto);
                intent.putExtra("PHONE", "54250705");
                startActivity(intent);
                finish();
            }
        }
    }

    private void goBackPressed() {
        getOnBackPressedDispatcher()
                .addCallback(
                        this,
                        new OnBackPressedCallback(true) {
                            @Override
                            public void handleOnBackPressed() {
                                launchMainActivity(null);
                            }
                        });
    }

    private void launchMainActivity(View view) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
