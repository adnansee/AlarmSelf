package com.eddy.app.alarmself.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import com.eddy.app.alarmself.alarm.Alarm;
import com.eddy.app.alarmself.db.DatabaseManager;

import org.joda.time.DateTime;




public class StartTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private int mHourOfDay;
    private int mMinute;
    private AlarmFragment alarmFragment;
    private Alarm alarm;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle bundle = getArguments();

        if (bundle != null) {
            alarm = bundle.getParcelable(Alarm.TAG);

            if (alarm != null) {
                return new MyTimePicker(getActivity(), this, alarm.getDateTime().getHourOfDay(), alarm.getDateTime().getMinuteOfHour(), true);
            }
        }

        final DateTime currentDateTime = new DateTime(System.currentTimeMillis());
        return new MyTimePicker(getActivity(), this, currentDateTime.getHourOfDay(), currentDateTime.getMinuteOfHour(), true);
    }

    public void setAlarmFragment(AlarmFragment alarmFragment) {
        this.alarmFragment = alarmFragment;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        boolean createNewAlarm = false;

        if (alarm == null) {
            alarm = new Alarm();
            alarm.setActive(false);
            createNewAlarm = true;
        }

        DateTime setTime = DateTime.now();
        setTime = setTime.withHourOfDay(mHourOfDay);
        setTime = setTime.withMinuteOfHour(mMinute);
        setTime = setTime.withSecondOfMinute(0);

        // user set a time in the past (for the next day)
        if (setTime.isBeforeNow()) {
            setTime = setTime.plusDays(1);
        }

        alarm.setDateTime(setTime);
        alarm.setShouldVibrate(true);
        alarm.setName("TimePicker Alarm");
        alarm.schedule(getContext());

        if (createNewAlarm) {
            DatabaseManager.create(alarm);
        } else {
            DatabaseManager.update(alarm);
        }

        if (alarmFragment != null) {
            alarmFragment.updateAlarmList();
        }
    }

    private class MyTimePicker extends TimePickerDialog {

        public MyTimePicker(Context context, OnTimeSetListener listener, int hourOfDay, int minute, boolean is24HourView) {
            super(context, listener, hourOfDay, minute, is24HourView);
        }

        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            super.onTimeChanged(view, hourOfDay, minute);
            mHourOfDay = hourOfDay;
            mMinute = minute;
        }
    }
}
