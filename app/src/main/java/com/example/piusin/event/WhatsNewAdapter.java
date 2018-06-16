package com.example.piusin.event;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.piusin.event.InterfacesPackage.SweetAlertClass;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Piusin on 2/17/2018.
 */

public class WhatsNewAdapter extends RecyclerView.Adapter<WhatsNewAdapter.MyViewHolder> implements SweetAlertClass{
    private Context mCtx;

    ArrayList<WhatsNewDataProvider> data = new ArrayList<>();
    WhatsNewDataProvider dataProvider;

    public WhatsNewAdapter(Context mCtx, ArrayList<WhatsNewDataProvider> data) {
        this.data = data;
        this.mCtx = mCtx;

    }

    @Override
    public WhatsNewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.whatsnew_singleitem, parent, false);
        return new WhatsNewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WhatsNewAdapter.MyViewHolder holder, int position) {
        dataProvider = data.get(position);
        holder.whatsNewImage.setImageResource(data.get(position).getWhatsNewImage());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView whatsNewImage;
        CardView whatsNew;

        public MyViewHolder(View itemView) {
            super(itemView);

            whatsNewImage = itemView.findViewById(R.id.whatsnew_single);
            whatsNew = itemView.findViewById(R.id.cardview_whats);
            whatsNew.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(IsInternetOn()!= true) {
            displayNoInternetAlert();
            } else {

                switch (v.getId()) {
                    case R.id.cardview_whats:
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new WhatsNewFragment()).addToBackStack(null).commit();
                        activity.getSupportActionBar().setTitle("WhatsNew");
                        break;

                    default:
                        Toast.makeText(mCtx, "End of Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }

    @Override
    public void displayNoInternetAlert() {
        new SweetAlertDialog(mCtx, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Network Problem")
                .setContentText("Looks like you are offline!! " + "\n" + "Check Internet Connection..for better application use")
                .setConfirmText("Ok!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    public boolean IsInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) mCtx.getSystemService(mCtx.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            //Toast.makeText(this, "There is internet", Toast.LENGTH_SHORT).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            return false;
        }
        return false;
    }

}