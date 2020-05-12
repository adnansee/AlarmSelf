package com.eddy.app.alarmself.util;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eddy.app.alarmself.R;
import com.eddy.app.alarmself.alarm.Alarm;


public class ItemViewHolder extends RecyclerView.ViewHolder {

    private final CheckBox checkBox;
    private final TextView alarmTimeView;
    private Alarm alarm;

    public ItemViewHolder(View itemView) {
        super(itemView);

        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_alarm_active);
        alarmTimeView = (TextView) itemView.findViewById(R.id.textView_alarm_time);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public TextView getAlarmTimeView() {
        return alarmTimeView;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
