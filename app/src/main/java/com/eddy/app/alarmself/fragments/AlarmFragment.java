package com.eddy.app.alarmself.fragments;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eddy.app.alarmself.R;
import com.eddy.app.alarmself.alarm.Alarm;
import com.eddy.app.alarmself.alarm.AlarmListAdapter;
import com.eddy.app.alarmself.db.DatabaseManager;
import com.eddy.app.alarmself.util.MyItemTouchHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

public class AlarmFragment extends Fragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private AnimatedVectorDrawable animation;
    private Drawable activated;
    private Drawable deactivated;
    private int position;
    private AlarmListAdapter adapter;
    private RecyclerView recyclerView;
    private AnimatedVectorDrawable checkedToUnchecked;
    private AnimatedVectorDrawable uncheckedToChecked;

    private Animation showFab;
    private FloatingActionButton fab;
    private Button deleteButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm, container, false);

        DatabaseManager.init(getContext());

        adapter = new AlarmListAdapter(this);

        if (getActivity() != null) {
            tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        }

        deactivated = getResources().getDrawable(R.drawable.ic_access_alarms_black_deactivated_24dp);
        animation = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.asl_pathmorph_arrowoverflow);
        checkedToUnchecked = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_pathmorph_crosstick_tick_to_cross);
        uncheckedToChecked = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_pathmorph_crosstick_cross_to_tick);
        showFab = AnimationUtils.loadAnimation(getContext(), R.anim.fab1_show);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(animation);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() == null) {
            return;
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new ItemTouchHelper(new MyItemTouchHelper(0, ItemTouchHelper.RIGHT, this)).attachToRecyclerView(recyclerView);

        updateAlarmList();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                StartTimePickerFragment fragment = new StartTimePickerFragment();
                fragment.setAlarmFragment(AlarmFragment.this);
                assert getFragmentManager() != null;
                fragment.show(getFragmentManager(), "timePicker");
            }
        });


        showFab();


       /* deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Alarm alarm = (Alarm) adapter.getItem(view.getId());
                if (alarm != null) {
                    alarm.cancelAlarm(getContext());
                    DatabaseManager.deleteEntry(alarm);
                }

                updateAlarmList();
            }
        });*/




    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (tabLayout == null || animation == null) {
            return;
        }

        if (tabLayout.getTabAt(position) == null) {
            return;
        }

        if (isVisibleToUser) {
            // start animation
            updateAlarmList();
            activateIcon();
            showFab();
        } else {
            // deactivated icon
            deactivateIcon();
        }
    }

    private void showFab() {
        if (fab == null) {
            return;
        }

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fab.getLayoutParams();
//        layoutParams.rightMargin += (int) (fab.getWidth() * 1.7);
//        layoutParams.bottomMargin += (int) (fab.getHeight() * 0.25);
//        fab.setLayoutParams(layoutParams);
        fab.startAnimation(showFab);
        fab.setClickable(true);
    }

    private void activateIcon() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tabLayout.getTabAt(position).setIcon(animation);
        }
        animation.start();
    }


    private void deactivateIcon() {
        animation.stop();
        tabLayout.getTabAt(position).setIcon(deactivated);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AlarmListAdapter getAdapter() {
        return adapter;
    }

    public void updateAlarmList(){
        final List<Alarm> alarms = DatabaseManager.getAll();
        adapter.setAlarmList(alarms);

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // reload content
                    adapter.notifyDataSetChanged();
                    if (alarms.size() > 0) {
                        getActivity().findViewById(android.R.id.empty).setVisibility(View.INVISIBLE);
                    } else {
                        getActivity().findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        deleteButton = (Button) view.findViewById(R.id.delButton);

        switch (view.getId()) {
            case R.id.checkBox_alarm_active:
                CheckBox checkBox = (CheckBox) view;

                Alarm alarm = (Alarm) adapter.getItem((Integer) checkBox.getTag());
                alarm.setActive(checkBox.isChecked());
                DatabaseManager.update(alarm);
                //callMathAlarmScheduleService();
                if (checkBox.isChecked()) {
                    checkedToUnchecked.stop();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBox.setButtonDrawable(uncheckedToChecked);
                    }
                    uncheckedToChecked.start();
                    alarm.reset();
                    alarm.schedule(getContext());
//                    Toast.makeText(getActivity(), alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                } else {
                    uncheckedToChecked.stop();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        checkBox.setButtonDrawable(checkedToUnchecked);
                    }
                    checkedToUnchecked.start();
                }
                break;
            case R.id.textView_alarm_time:

            case R.id.textView_alarm_days:
                final Bundle bundle = new Bundle();
                bundle.putParcelable(Alarm.TAG, (Alarm) adapter.getItem((Integer) view.getTag()));
                StartTimePickerFragment fragment = new StartTimePickerFragment();
                fragment.setAlarmFragment(AlarmFragment.this);
                fragment.setArguments(bundle);
                fragment.show(getFragmentManager(), "timePicker");
                break;

            case R.id.delButton:
              alarm = (Alarm) adapter.getItem(view.getId());
                alarm.cancelAlarm(getContext());
               DatabaseManager.deleteEntry(view.getId());
                updateAlarmList();

                break;

        }
        updateAlarmList();

    }

    private void deleteAlarm(View view) {
        Alarm alarm1 = (Alarm) adapter.getItem(view.getId());
        if (alarm1 != null) {
            alarm1.cancelAlarm(getContext());
            DatabaseManager.deleteEntry(alarm1);
        }

        updateAlarmList();
    }


}
