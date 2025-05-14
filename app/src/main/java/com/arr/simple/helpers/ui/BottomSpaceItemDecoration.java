package com.arr.simple.helpers.ui;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int bottomSpace;
    private boolean isReverseLayout;

    public BottomSpaceItemDecoration(int bottomSpace, boolean isReverseLayout) {
        this.bottomSpace = bottomSpace;
        this.isReverseLayout = isReverseLayout;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if ((!isReverseLayout && position == state.getItemCount() - 1)
                || (isReverseLayout && position == 0)) {
            outRect.bottom = bottomSpace;
        }
    }
}
