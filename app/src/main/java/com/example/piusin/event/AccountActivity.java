package com.example.piusin.event;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class AccountActivity extends AppCompatActivity {

    Toolbar toolbar; //    <uses-permission android:name="android.permission.MANAGE_DOCUMENTS" />
    ViewPager viewPager;
    TabLayout tabLayout;
    SwipeAdapter swipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tabLayout);

        viewPager = findViewById(R.id.viewpager);
        swipeAdapter = new SwipeAdapter(getSupportFragmentManager());
        swipeAdapter.addFragments(new FragmentAccountDetails(), "Account Details");
        swipeAdapter.addFragments(new MyAccountFragment(), "Payment History");
        viewPager.setAdapter(swipeAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
}
