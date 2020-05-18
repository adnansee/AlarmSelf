package com.eddy.app.alarmself.fragments;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.eddy.app.alarmself.R;
import com.google.android.material.tabs.TabLayout;


public class SettingsFragment extends Fragment implements View.OnClickListener {


    private int position;
    private ImageButton submitButton;

    public SharedPreferences sharedpreferences;
    private EditText locationText;
    private EditText alarmMaxDur;
    public static final String mypreference = "mypref";
    public static final String Location = "locationKey";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        locationText = rootView.findViewById(R.id.locationText);
        submitButton = rootView.findViewById(R.id.submitButton);

        sharedpreferences = this.getActivity().getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        System.out.println(sharedpreferences.getString(Location, ""));
        if (sharedpreferences.contains(Location)) {
            locationText.setText(sharedpreferences.getString(Location, ""));
        }

                submitButton.setOnClickListener(v -> {
                    String l = String.valueOf(locationText.getText());
                    System.out.println(l);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Location, l);
                    editor.commit();
                });
        return rootView;
    }
    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onClick(@NonNull View v) {


            System.out.println("ERROR MATE");

    }
}
