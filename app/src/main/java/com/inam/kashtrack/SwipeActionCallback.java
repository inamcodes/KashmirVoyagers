package com.inam.kashtrack;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Handles right-swipe (delete) and left-swipe (edit) gestures on the cash
 * entry list, drawing a colored background + icon as the user swipes, and
 * delegating the actual action to the supplied {@link SwipeListener}.
 *
 * Date header rows are skipped (not swipeable) by returning 0 movement
 * flags for them.
 */
public class SwipeActionCallback extends ItemTouchHelper.SimpleCallback {

    public interface SwipeListener {
        void onSwipedRight(CashEntry entry, int position); // delete
        void onSwipedLeft(CashEntry entry, int position);  // edit
    }

    private final CashEntryAdapter adapter;
    private final SwipeListener listener;

    private final Paint deletePaint = new Paint();
    private final Paint editPaint = new Paint();
    private final Drawable deleteIcon;
    private final Drawable editIcon;

    public SwipeActionCallback(android.content.Context context, CashEntryAdapter adapter, SwipeListener listener) {
        super(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.adapter = adapter;
        this.listener = listener;
        deletePaint.setColor(ContextCompat.getColor(context, R.color.red_out));
        editPaint.setColor(ContextCompat.getColor(context, R.color.header_orange_end));
        deleteIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_delete);
        editIcon = ContextCompat.getDrawable(context, android.R.drawable.ic_menu_edit);
        if (deleteIcon != null) deleteIcon.setTint(Color.WHITE);
        if (editIcon != null) editIcon.setTint(Color.WHITE);
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION || adapter.getEntryAt(position) == null) {
            // Header rows: disable swiping.
            return makeMovementFlags(0, 0);
        }
        return super.getMovementFlags(recyclerView, viewHolder);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) return;
        CashEntry entry = adapter.getEntryAt(position);
        if (entry == null) {
            adapter.notifyItemChanged(position);
            return;
        }
        if (direction == ItemTouchHelper.RIGHT) {
            listener.onSwipedRight(entry, position);
        } else {
            listener.onSwipedLeft(entry, position);
        }
        // Always redraw the row; the listener decides whether to actually
        // remove/update data (e.g. delete is gated behind a confirm dialog,
        // so we must restore the row here and let the dialog's positive
        // action drive the real removal via adapter refresh).
        adapter.notifyItemChanged(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                             @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                             int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int iconMargin = (itemView.getHeight() - (deleteIcon != null ? deleteIcon.getIntrinsicHeight() : 0)) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - (deleteIcon != null ? deleteIcon.getIntrinsicHeight() : 0)) / 2;
        int iconBottom = iconTop + (deleteIcon != null ? deleteIcon.getIntrinsicHeight() : 0);

        if (dX > 0) {
            // Swiping right -> delete (red background, left-aligned icon)
            RectF background = new RectF(itemView.getLeft(), itemView.getTop(), dX, itemView.getBottom());
            c.drawRect(background, deletePaint);
            if (deleteIcon != null) {
                int iconLeft = itemView.getLeft() + iconMargin;
                int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteIcon.draw(c);
            }
        } else if (dX < 0) {
            // Swiping left -> edit (orange background, right-aligned icon)
            RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            c.drawRect(background, editPaint);
            if (editIcon != null) {
                int iconRight = itemView.getRight() - iconMargin;
                int iconLeft = iconRight - editIcon.getIntrinsicWidth();
                editIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                editIcon.draw(c);
            }
        }
    }
}
