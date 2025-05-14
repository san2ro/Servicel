package com.arr.simple.fragment.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.arr.simple.R;
import com.arr.simple.activities.AboutActivity;
import com.arr.simple.activities.LoginActivity;
import com.arr.simple.activities.MyQrCodeActivity;
import com.arr.simple.activities.ReportActivity;

import com.arr.simple.activities.ui.DonationActivity;
import com.arr.simple.activities.ui.SettingsActivity;
import com.arr.simple.adapters.PromotionAdapter;
import com.arr.simple.databinding.FragmentHomeBinding;
import com.arr.simple.helpers.contacts.ContactsManager;
import com.arr.simple.helpers.notification.BalanceNotification;
import com.arr.simple.helpers.preferences.SPHelper;
import com.arr.simple.helpers.profile.ProcessProfile;
import com.arr.simple.helpers.ussd.USSDUtils;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import cu.arr.etecsa.api.etecsa.promo.OperatorPromo;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private String pendingPhoneNumber;

    private String adelanta = null;

    private ActivityResultLauncher<Intent> contactTransfer;
    private ActivityResultLauncher<Intent> contactAsterisco;
    private ActivityResultLauncher<Intent> contactPrivado;
    private ActivityResultLauncher<String> cameraPermission;

    private ScannerViewModel viewModel;

    private int currentPosition = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable scrollRunnable =
            new Runnable() {
                @Override
                public void run() {
                    RecyclerView.Adapter adapter = binding.recycler.getAdapter();
                    if (adapter == null || adapter.getItemCount() == 0) return;

                    currentPosition = (currentPosition + 1) % adapter.getItemCount();
                    binding.recycler.smoothScrollToPosition(currentPosition);
                    handler.postDelayed(this, 5000);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // seleccionar contacto para transferir saldo
        contactTransfer =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> handlerContactResult(result, 1));
        contactAsterisco =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> handlerContactResult(result, 2));
        contactPrivado =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> handlerContactResult(result, 3));

        cameraPermission =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                NavController nav = NavHostFragment.findNavController(this);
                                nav.navigate(R.id.scanner);
                            }
                        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle arg2) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        requestPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted && pendingPhoneNumber != null) {
                                new USSDUtils(getContext())
                                        .executeUSSD(pendingPhoneNumber + Uri.encode("#"));
                                pendingPhoneNumber = null;
                            } else if (!isGranted) {
                                showMessage("Permiso no concedido");
                            }
                        });
        // consultar saldo general
        binding.statusButtons.btnSaldo.setOnClickListener(
                v -> executeCall("*222" + Uri.encode("#")));
        // consultar bonos
        binding.statusButtons.btnBono.setOnClickListener(
                v -> executeCall("*222*266" + Uri.encode("#")));
        // consultar datos
        binding.statusButtons.btnDatos.setOnClickListener(
                v -> executeCall("*222*328" + Uri.encode("#")));
        // consultar amigo
        binding.statusButtons.btnAmigo.setOnClickListener(
                v -> executeCall("*222*264" + Uri.encode("#")));

        // recargar saldo
        binding.btnRecarga.setOnClickListener(this::recargarSaldo);
        binding.layoutRecarga.setEndIconOnClickListener(this::setScannerQR);

        binding.editRecarga.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_GO) {
                        recargarSaldo(null);
                        hideKeyboard(requireContext(), binding.editRecarga);
                        return true;
                    }
                    return false;
                });

        binding.editRecarga.addTextChangedListener(new ValidateCodeRecarga());
        binding.layoutPhone.setEndIconOnClickListener(this::selectPhoneNumberTransfer);
        // menu
        binding.menu.setOnClickListener(this::showMenu);

        // llamar con asterisco 99
        binding.call.btnRevertido.setOnClickListener(this::selectPhoneNumberAsterisco);
        // llamar con número privado
        binding.call.btnPrivado.setOnClickListener(this::selectPhoneNumberPrivado);

        // adelanta saldo
        String phone = new SPHelper(requireContext()).getString("phone", "");
        String deuda = ProcessProfile.getStatusAdelanta(requireContext(), phone);
        binding.deuda.setText(deuda == null || deuda.isEmpty() ? "" : deuda);
        binding.btnAdelanta.setOnClickListener(this::adelantaSaldo);

        // transferir
        binding.editMonto.addTextChangedListener(new ValidateMonto());
        binding.btnTransferir.setOnClickListener(this::transferirSaldo);
        String donateMonto = requireActivity().getIntent().getStringExtra("MONTO");
        String donateNumber = requireActivity().getIntent().getStringExtra("PHONE");
        if (!TextUtils.isEmpty(donateNumber) && !TextUtils.isEmpty(donateMonto)) {
            binding.editPhone.setText(donateNumber);
            binding.editMonto.setText(donateMonto);
        }

        // obtener el código escaneado
        viewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        viewModel.getText().observe(getViewLifecycleOwner(), s -> binding.editRecarga.setText(s));

        // banner
        boolean showBanner = new SPHelper(requireContext()).getBoolean("banner", false);
        if (showBanner) {
            OperatorPromo.async(
                    requireActivity(),
                    promo -> {
                        if (!promo.isEmpty()) {
                            if (isAdded()) {
                                PromotionAdapter adapter =
                                        new PromotionAdapter(requireContext(), promo);
                                binding.recycler.setAdapter(adapter);
                                binding.recycler.setLayoutManager(
                                        new CarouselLayoutManager(new HeroCarouselStrategy()));
                                handler.postDelayed(scrollRunnable, 5000);
                            }
                        }
                    });
        }

        boolean notFirstLogin = new SPHelper(requireContext()).getBoolean("firstAccess", true);
        if (!notFirstLogin) {
            binding.consumo.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    private void recargarSaldo(View view) {
        String value = binding.editRecarga.getText().toString().trim();
        if (!TextUtils.isEmpty(value)) {
            executeCall("*662*" + value + Uri.encode("#"));
            binding.editRecarga.getText().clear();
        } else {
            hideKeyboard(requireContext(), binding.editRecarga);
            showMessage("Por favor, rellene toda la información requerida antes de continuar.");
        }
    }

    private void transferirSaldo(View view) {
        String phone = binding.editPhone.getText().toString();
        String clave = binding.editClave.getText().toString();
        String monto = binding.editMonto.getText().toString();
        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(clave)) {
            executeCall("*234*1*" + phone + "*" + clave + "*" + monto + Uri.encode("#"));
            binding.editPhone.getText().clear();
            binding.editClave.getText().clear();
            binding.editMonto.getText().clear();
        } else {
            showMessage("El campo Número y Clave son obligatorios");
            hideKeyboard(requireContext(), binding.editPhone);
        }
    }

    private void adelantaSaldo(View view) {
        String value = null;
        switch (binding.toggleButton.getCheckedButtonId()) {
            case R.id.option1:
                value = "25";
                break;
            case R.id.option2:
                value = "50";
                break;
        }

        if (!TextUtils.isEmpty(value)) {
            executeCall("*234*3*1*" + value + Uri.encode("#"));
        }
    }

    private void executeCall(String value) {
        if (isPermissionCallGrated()) {
            new USSDUtils(getContext()).executeUSSD(value);
        } else {
            pendingPhoneNumber = value;
            requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
        }
    }

    private void setScannerQR(View view) {
        if (getCameraPermission()) {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.scanner);
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA);
        }
    }

    private boolean isPermissionCallGrated() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean getCameraPermission() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void showMessage(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private void showMenu(View view) {
        PopupMenu menu = new PopupMenu(requireContext(), view);
        menu.inflate(R.menu.main_menu);
        menu.setForceShowIcon(true);

        boolean notFirstLogin = new SPHelper(requireContext()).getBoolean("firstAccess", true);
        MenuItem scanner = menu.getMenu().findItem(R.id.menu_scanner);
        MenuItem login = menu.getMenu().findItem(R.id.menu_login);
        login.setVisible(notFirstLogin);
        scanner.setVisible(!notFirstLogin);

        menu.setOnMenuItemClickListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_login:
                            startActivity(new Intent(requireActivity(), LoginActivity.class));
                            requireActivity().finish();
                            return true;
                        case R.id.menu_scanner:
                            startActivity(new Intent(requireActivity(), MyQrCodeActivity.class));
                            requireActivity()
                                    .overridePendingTransition(
                                            R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
                            requireActivity().finish();
                            return true;
                        case R.id.menu_settings:
                            startActivity(new Intent(requireActivity(), SettingsActivity.class));
                            requireActivity()
                                    .overridePendingTransition(
                                            R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
                            requireActivity().finish();
                            return true;
                        case R.id.menu_report:
                            startActivity(new Intent(requireActivity(), ReportActivity.class));
                            requireActivity()
                                    .overridePendingTransition(
                                            R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
                            requireActivity().finish();
                            return true;
                        case R.id.menu_donate:
                            startActivity(new Intent(requireActivity(), DonationActivity.class));
                            requireActivity()
                                    .overridePendingTransition(
                                            R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
                            requireActivity().finish();
                            return true;
                        case R.id.menu_about:
                            startActivity(new Intent(requireActivity(), AboutActivity.class));
                            requireActivity()
                                    .overridePendingTransition(
                                            R.anim.shared_axis_z_in, R.anim.shared_axis_z_out);
                            requireActivity().finish();
                            return true;
                        default:
                            return false;
                    }
                });
        menu.show();
    }

    private void selectPhoneNumberTransfer(View view) {
        selectContact(contactTransfer);
    }

    private void selectPhoneNumberAsterisco(View view) {
        selectContact(contactAsterisco);
    }

    private void selectPhoneNumberPrivado(View view) {
        selectContact(contactPrivado);
    }

    private void selectContact(ActivityResultLauncher<Intent> launcher) {
        Intent intent =
                new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        launcher.launch(intent);
    }

    private void handlerContactResult(ActivityResult result, int id) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri contactUri = result.getData().getData();
            Pair<String, String> pairContact =
                    new ContactsManager(requireContext()).getContactInfo(contactUri);
            String name = pairContact.first;
            String phone = pairContact.second;
            switch (id) {
                case 1:
                    binding.editPhone.setText(phone);
                    binding.layoutPhone.setHelperText(name);
                    break;
                case 2:
                    if (phone != null) {
                        executeCall("*99" + phone);
                    }
                    break;
                case 3:
                    if (phone != null) {
                        executeCall(Uri.encode("#") + "31" + Uri.encode("#") + phone);
                    }
                    break;
            }
        }
    }

    public class ValidateCodeRecarga implements TextWatcher {

        private boolean lock;

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() < 19) {
                binding.layoutRecarga.setErrorEnabled(true);
                binding.layoutRecarga.setError("El número debe tener 16 dígitos");
            } else {
                binding.layoutRecarga.setErrorEnabled(false);
                binding.layoutRecarga.setError("");
            }
            if (s.toString().isEmpty()) {
                binding.layoutRecarga.setErrorEnabled(false);
                binding.layoutRecarga.setError("");
                binding.editRecarga.clearFocus();
                hideKeyboard(requireContext(), binding.editRecarga);
            }
            if (lock || s.toString().replaceAll("\\s", "").length() > 16) {
                return;
            }
            lock = true;
            int len = s.length();
            for (int i = len - 1; i >= 0; i--) {
                if (s.toString().charAt(i) == ' ') {
                    s.delete(i, i + 1);
                }
            }
            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            lock = false;
        }
    }

    public class ValidateMonto implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

        @Override
        public void afterTextChanged(Editable s) {
            String value = s.toString();
            if (value.contains(".")) {
                binding.editMonto.getText().clear();
                hideKeyboard(requireContext(), binding.editMonto);
                showMessage("Para enviar centavos déje el monto vacío");
            }
        }
    }

    private void hideKeyboard(Context context, TextInputEditText ediText) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ediText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        handler.removeCallbacks(scrollRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(scrollRunnable);
    }
}
