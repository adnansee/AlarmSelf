package com.eddy.app.alarmself.alarm;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eddy.app.alarmself.R;
import com.eddy.app.alarmself.fragments.AlarmFragment;
import com.eddy.app.alarmself.util.ItemViewHolder;

import java.util.ArrayList;
import java.util.List;


public class AlarmListAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private AlarmFragment alarmFragment;
    private List<Alarm> alarmList = new ArrayList<>();
    private AnimatedVectorDrawable checkedToUnchecked;
    private AnimatedVectorDrawable uncheckedToChecked;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AlarmListAdapter(AlarmFragment alarmFragment) {
        this.alarmFragment = alarmFragment;
        checkedToUnchecked = (AnimatedVectorDrawable) alarmFragment.getContext().getDrawable(R.drawable.avd_pathmorph_crosstick_tick_to_cross);
        uncheckedToChecked = (AnimatedVectorDrawable) alarmFragment.getContext().getDrawable(R.drawable.avd_pathmorph_crosstick_cross_to_tick);
    }

    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
    }

    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_element, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        final Alarm alarm = (Alarm) getItem(position);

        final CheckBox checkBox = holder.getCheckBox();
        checkBox.setChecked(alarm.isActive());
        checkBox.setTag(position);
        checkBox.setOnClickListener(alarmFragment);

        Log.d("adapter", "alarm is " + alarm.isActive() + ", " + alarm.getAlarmTimeStringParcelable());

        final TextView alarmTimeView = holder.getAlarmTimeView();
        alarmTimeView.setText(alarm.getAlarmTimeString());
        alarmTimeView.setOnClickListener(alarmFragment);
        alarmTimeView.setTag(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}
