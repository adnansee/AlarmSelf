package com.eddy.app.alarmself;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link //android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * // * {@link //android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Drawable background;
    private giveRandomPic giveRandomPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //background = giveRandomPic.doInBackground();


       // mViewPager.setBackgroundDrawable(background);

    }

    public void givePic() {
        URL url = null;
        try {
            url = new URL("https://picsum.photos/200/300");
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
            mViewPager.setBackgroundDrawable(background);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("StaticFieldLeak")
    private class giveRandomPic extends AsyncTask<URL, Integer, Drawable> {

        URL url = new URL("https://picsum.photos/200/300");

        private giveRandomPic() throws MalformedURLException {}

        protected Drawable doInBackground(URL... urls) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable background = new BitmapDrawable(getResources(), bitmap);
            return background;}

        protected void onProgressUpdate(Integer... progress) {}
        protected void onPostExecute(Drawable result) {}
    }

}
