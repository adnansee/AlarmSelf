package com.eddy.app.alarmself.util;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.app.alarmself.alarm.Alarm;
import com.eddy.app.alarmself.db.DatabaseManager;
import com.eddy.app.alarmself.fragments.AlarmFragment;

public class MyItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private AlarmFragment alarmFragment;

    public MyItemTouchHelper(int dragDirs, int swipeDirs, AlarmFragment alarmFragment) {
        super(dragDirs, swipeDirs);
        this.alarmFragment = alarmFragment;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        final Alarm alarm = (Alarm) alarmFragment.getAdapter().getItem(viewHolder.getAdapterPosition());

        if (alarm != null) {
            alarm.cancelAlarm(alarmFragment.getContext());
            DatabaseManager.deleteEntry(alarm);
        }

        alarmFragment.updateAlarmList();
    }

}
