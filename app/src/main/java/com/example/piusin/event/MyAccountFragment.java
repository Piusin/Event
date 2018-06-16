package com.example.piusin.event;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;
    SwipeAdapter swipeAdapter;
    AppCompatActivity activity;


    public MyAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myaccount, container, false);
        tabLayout = view.findViewById(R.id.tabLayout);
        activity = (AppCompatActivity) view.getContext();
        viewPager = view.findViewById(R.id.viewpager);
        swipeAdapter = new SwipeAdapter(activity.getSupportFragmentManager());
        swipeAdapter.addFragments(new FragmentAccountDetails(), "Account Details");
        swipeAdapter.addFragments(new PaymentHistory(), "Payment History");
        viewPager.setAdapter(swipeAdapter);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

}
