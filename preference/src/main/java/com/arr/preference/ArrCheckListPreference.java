package com.arr.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashSet;
import java.util.Set;

public class ArrCheckListPreference extends MultiSelectListPreference {

    private Set<String> values = new HashSet<>(); // Variable para manejar los valores seleccionados

    /**
     * Constructor para inicializar la preferencia.
     *
     * @param context Contexto de la aplicación
     * @param attrs Atributos personalizados definidos en XML
     */
    public ArrCheckListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.layout_view_preference); // Layout personalizado opcional
    }

    /**
     * Método para manejar el evento de clic en la preferencia. Muestra el diálogo de selección
     * múltiple.
     */
    @Override
    protected void onClick() {
        // Sincronizar los valores antes de abrir el diálogo
        values.clear();
        values.addAll(getValues());

        // Construcción del diálogo
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(getDialogTitle());
        final CharSequence[] entries = getEntries();
        final CharSequence[] entryValues = getEntryValues();
        final boolean[] checkedItems = getSelectedItems();

        // Configuración de los ítems seleccionables
        builder.setMultiChoiceItems(
                entries,
                checkedItems,
                (dlg, which, isChecked) -> {
                    if (isChecked) values.add(entryValues[which].toString());
                    else values.remove(entryValues[which].toString());

                    // Llamar al listener para manejar cambios
                    if (callChangeListener(values)) {
                        setValues(values); // Guardar los valores seleccionados
                    }
                });

        // Botón de aceptar
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    /**
     * Método para actualizar la vista de la preferencia en el RecyclerView de ajustes.
     *
     * @param holder Holder de la preferencia
     */
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        // Opcional: Configura los elementos visuales como resumen, título e ícono
        final TextView summaryView = (TextView) holder.findViewById(android.R.id.summary);
        if (summaryView != null) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }

        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        final ImageView imageView = (ImageView) holder.findViewById(android.R.id.icon);
        if (imageView != null) {
            final Drawable icon = getIcon();
            if (icon != null) {
                imageView.setImageDrawable(icon);
                imageView.setVisibility(View.VISIBLE);
            } else {
                if (isIconSpaceReserved()) {
                    imageView.setVisibility(View.INVISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * Método para inicializar los valores de la preferencia al cargar.
     *
     * @param defaultValue Valor predeterminado definido en XML
     */
    @Override
    protected void onSetInitialValue(Object defaultValue) {
        // Obtener el valor persistido o el valor predeterminado
        Set<String> initialValues =
                getPersistedStringSet(
                        defaultValue == null ? new HashSet<>() : (Set<String>) defaultValue);

        // Configurar los valores seleccionados
        setValues(initialValues);
        values.clear();
        values.addAll(initialValues); // Sincronizar con el estado interno
    }

    /**
     * Método para determinar qué ítems están seleccionados actualmente.
     *
     * @return Array de booleanos indicando los ítems seleccionados
     */
}
