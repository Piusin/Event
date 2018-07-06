package com.example.piusin.event;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.piusin.event.HomeFragment.getDiscountData;
import static com.example.piusin.event.HomeFragment.getHorizontalData;
import static com.example.piusin.event.HomeFragment.getVerticalData;
import static com.example.piusin.event.HomeFragment.getWhatsNewData;

/**
 * Created by Piusin on 1/31/2018.
 */

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private ArrayList<Object> items;
    private final int VERTICAL = 0;
    private final int HORIZONTAL = 1;
    private final int DISCOUNT = 2;
    private final int WHATSNEW = 3;

    public MainAdapter(Context context, ArrayList<Object> items) {
        this.context = context;
        this.items = items;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case VERTICAL:
                view = inflater.inflate(R.layout.categories, parent, false);
                holder = new VerticalViewHolder(view);
                break;
            case HORIZONTAL:
                view = inflater.inflate(R.layout.featured_stores, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
            case DISCOUNT:
                view = inflater.inflate(R.layout.discount_layout,parent,false);
                holder = new AdvertViewHolder(view);
                break;
            case WHATSNEW:
                view = inflater.inflate(R.layout.whatsnew_layout,parent,false);
                holder = new WhatsNewViewHolder(view);
                break;

            default:
                view = inflater.inflate(R.layout.categories, parent, false);
                holder = new HorizontalViewHolder(view);
                break;
        }

        return holder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder.getItemViewType() == VERTICAL)
            verticalView((VerticalViewHolder) holder);
        else if (holder.getItemViewType() == HORIZONTAL)
            horizontalView((HorizontalViewHolder) holder);
        else if(holder.getItemViewType() == DISCOUNT)
            advertView((AdvertViewHolder) holder);
        else if(holder.getItemViewType() == WHATSNEW)
            whatsNewView((WhatsNewViewHolder) holder);
    }

    private void verticalView(VerticalViewHolder holder) {
        CategoriesAdapter adapter1 = new CategoriesAdapter(context, getVerticalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter1);
    }


    private void horizontalView(HorizontalViewHolder holder) {
        FeaturedStoreMainAdapter adapter = new FeaturedStoreMainAdapter(context,getHorizontalData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerView.setAdapter(adapter);
    }

    private void advertView(AdvertViewHolder holder) {
        DiscountMainAdapter adapter = new DiscountMainAdapter(context,getDiscountData());
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
       // holder.recyclerView.setLayoutManager(new GridLayoutManager(context,2,VERTICAL,false));
        holder.recyclerView.setAdapter(adapter);
    }

    private void whatsNewView(WhatsNewViewHolder holder) {
        WhatsNewAdapter adapter = new WhatsNewAdapter(context,getWhatsNewData());
       // holder.recyclerView.setLayoutManager(new GridLayoutManager(context,2,VERTICAL,false));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        holder.recyclerView.setAdapter(adapter);
    }



    @Override
    public int getItemCount() {
        return items.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof CategoriesDataProvider)
            return VERTICAL;
        if (items.get(position) instanceof FeaturedStoreDataProvider)
            return HORIZONTAL;
        if(items.get(position) instanceof DiscountMainDataProvider)
            return DISCOUNT;
        if(items.get(position) instanceof WhatsNewDataProvider)
            return WHATSNEW;
        return -1;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder {

        RecyclerView recyclerView;
        TextView textView;

        HorizontalViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
            textView = itemView.findViewById(R.id.categories_title);
        }
    }

    public class VerticalViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        VerticalViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
        }
    }

    public class AdvertViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        AdvertViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
        }
    }

    public class WhatsNewViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        WhatsNewViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.inner_recyclerView);
        }
    }
}
