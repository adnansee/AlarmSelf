package com.eddy.app.alarmself.util;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

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
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
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
