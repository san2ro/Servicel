package com.arr.simple.helpers.ui;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import androidx.appcompat.widget.PopupMenu;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class NumberList {

    public interface OnItemSelected {
        void onSelectItem(String phone);
    }

    private Context mContext;

    public NumberList(Context context) {
        this.mContext = context;
    }

    public void showMenu(View view, List<String> list, OnItemSelected listener) {
        PopupMenu menu = new PopupMenu(mContext, view);

        // Configuración para versiones recientes de Android
        try {
            Field[] fields = menu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(menu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceShowIcon =
                            classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceShowIcon.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < list.size(); i++) {
            menu.getMenu().add(Menu.NONE, i, i, list.get(i).substring(2));
        }

        menu.setOnMenuItemClickListener(
                item -> {
                    String phone = list.get(item.getItemId());
                    listener.onSelectItem(phone);
                    return true;
                });

        menu.show();
    }
}
