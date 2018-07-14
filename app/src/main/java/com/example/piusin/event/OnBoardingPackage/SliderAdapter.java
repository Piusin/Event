package com.example.piusin.event.OnBoardingPackage;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.piusin.event.R;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ImageView isliderImage;
    TextView isliderHead;
    TextView isliderDes;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int [] sliderImages= {
            R.mipmap.account,
            R.mipmap.categories,
            R.mipmap.hom
    };

    public String [] sliderHeadings = {
       "Home Screen",
            "Product Screen",
            "Search Screen"
    };

    public String [] sliderDes = {
            "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh",
            "hgggggggggggggggggggggggggggggggggg",
            "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy"

    };

    @Override
    public int getCount() {
        return sliderHeadings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container, false);

        isliderImage = view.findViewById(R.id.slider_image);
        isliderHead = view.findViewById(R.id.sliderHead);
        isliderDes = view.findViewById(R.id.sliderDesc);

        isliderImage.setImageResource(sliderImages[position]);
        isliderHead.setText(sliderHeadings[position]);
        isliderDes.setText(sliderDes[position]);
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}
