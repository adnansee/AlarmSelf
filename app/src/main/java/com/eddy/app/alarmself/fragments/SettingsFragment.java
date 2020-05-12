package com.eddy.app.alarmself.fragments;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.eddy.app.alarmself.R;
import com.google.android.material.tabs.TabLayout;


public class SettingsFragment extends Fragment {

    private TabLayout tabLayout;
    private AnimatedVectorDrawable animation;
    private Drawable deactivated;
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        if (getActivity() != null) {
            tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);
        }

        deactivated = getResources().getDrawable(R.drawable.vd_pathmorph_arrowoverflow_overflow_deactivated);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animation = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_pathmorph_arrowoverflow_overflow_to_arrow);
        }

        tabLayout.getTabAt(position).setIcon(deactivated);

        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (tabLayout == null || deactivated == null || animation == null) {
            return;
        }

        if (tabLayout.getTabAt(position) == null) {
            return;
        }

        if (isVisibleToUser) {
            // start animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tabLayout.getTabAt(position).setIcon(animation);
            }
            animation.start();
        } else {
            // deactivated icon
            animation.stop();
            tabLayout.getTabAt(position).setIcon(deactivated);
        }
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
