package com.example.piusin.event.OnBoardingPackage;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.piusin.event.R;

public class MainOnboardActivity extends AppCompatActivity {
    private ViewPager msliderViewPager;
    private LinearLayout mdotslayout;

    private SliderAdapter sliderAdapter;
    private TextView []mdots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_onboard);

        msliderViewPager = findViewById(R.id.viewpager);
        mdotslayout = findViewById(R.id.dotslayout);

        sliderAdapter = new SliderAdapter(this);
        msliderViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        msliderViewPager.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicator(int position){
        mdots = new TextView[3];
        for(int i = 0; i<mdots.length; i++){
            mdots[i] = new TextView(this);
            mdots[i].setText(Html.fromHtml("&8226"));
            mdots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            mdotslayout.addView(mdots[i]);
        }

        if(mdots.length > 0){
            mdots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
          addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
