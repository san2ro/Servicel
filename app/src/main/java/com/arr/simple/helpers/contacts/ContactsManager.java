package com.arr.simple.helpers.contacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Pair;
import android.widget.Toast;

public class ContactsManager {

    private Context mContext;

    public ContactsManager(Context context) {
        this.mContext = context;
    }

    public Pair<String, String> getContactInfo(Uri contactUri) {
        String phoneNumber = null;
        String contactName = null;

        // Columnas que necesitamos
        String[] projection =
                new String[] {
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.TYPE
                };

        try (Cursor cursor =
                mContext.getContentResolver().query(contactUri, projection, null, null, null)) {

            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex =
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int typeIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);

                // Preferir números móviles si hay varios
                do {
                    int type = cursor.getInt(typeIndex);
                    String currentNumber = cursor.getString(numberIndex);
                    if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                        String formaterdNumber = validatePhoneNumber(currentNumber);
                        if (formaterdNumber != null) {
                            phoneNumber = formaterdNumber;
                            contactName = cursor.getString(nameIndex);
                        } else {
                            String name = cursor.getString(nameIndex);
                            showMessage(name);
                        }
                        break; // Preferir el primer número móvil
                    } else if (phoneNumber == null) {
                        String formaterdNumber = validatePhoneNumber(currentNumber);
                        if (formaterdNumber != null) {
                            phoneNumber = formaterdNumber;
                            contactName = cursor.getString(nameIndex);
                        } else {
                            String name = cursor.getString(nameIndex);
                            showMessage(name);
                        }
                    }
                } while (cursor.moveToNext());
            }
        }
        return new Pair<>(contactName, phoneNumber);
    }

    private String validatePhoneNumber(String rawNumber) {
        if (rawNumber == null) return null;

        // 1. Eliminar el prefijo +53 si existe
        String formattedNumber = rawNumber.replaceFirst("^\\+53", "");

        // 2. Eliminar todos los caracteres no numéricos
        formattedNumber = formattedNumber.replaceAll("[^0-9]", "");

        // 3. Validar si es un número cubano válido (8 dígitos que empieza con 5 o 6)
        if (formattedNumber.length() == 8
                && (formattedNumber.startsWith("5") || formattedNumber.startsWith("6"))) {
            return formattedNumber;
        }

        return null;
    }

    private void showMessage(String contactName) {
        Toast.makeText(mContext, contactName + " no posee un número válido", Toast.LENGTH_LONG)
                .show();
    }
}
