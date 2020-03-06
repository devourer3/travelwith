package com.mymusic.orvai.travel_with.Activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.mymusic.orvai.travel_with.R;
import com.mymusic.orvai.travel_with.fragment.Ar_Vr;
import com.mymusic.orvai.travel_with.fragment.Attraction;
import com.mymusic.orvai.travel_with.fragment.Conference;
import com.mymusic.orvai.travel_with.fragment.Etc;
import com.mymusic.orvai.travel_with.fragment.IamReporter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_bottom_bar)
    BottomNavigationView mainBottomBar;
    @BindView(R.id.main_fragment_container)
    FrameLayout mainFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mainBottomBar.setOnNavigationItemSelectedListener(this);
        loadFragment(new Attraction());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.bottom_navi_attraction:
                fragment = new Attraction();
                break;
            case R.id.bottom_navi_reporter:
                fragment = new IamReporter();
                break;
            case R.id.bottom_navi_conference:
                fragment = new Conference();
                break;
            case R.id.bottom_navi_ar_vr:
                fragment = new Ar_Vr();
                break;
            case R.id.bottom_navi_etc:
                fragment = new Etc();
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

}
