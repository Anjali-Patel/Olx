package gss.com.bsell.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gss.com.bsell.Model.UserProfileModel;
import gss.com.bsell.R;
import gss.com.bsell.ShowDetailsActivity;
import gss.com.bsell.Utility.CommonUtils;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileAdapter.MyViewHolder> {
    private Activity activity1;
    private ArrayList<UserProfileModel> discoverItemsArrayList;
    private String userdemand;


    public UserProfileAdapter(FragmentActivity activity, ArrayList<UserProfileModel> discoverItemsModelArrayList, String userdemand) {
        activity1 = activity;
        discoverItemsArrayList = discoverItemsModelArrayList;
        this.userdemand = userdemand;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView adapterName, adapterAddr,adapterEmail,adapterMobile,adapterCity,adapterstate,adapterPin,adapterCountry;
        ImageView adapterUserImage;

        public MyViewHolder(View v) {
            super(v);
            adapterName = (TextView) v.findViewById(R.id.adapterTextName);
            adapterAddr = (TextView) v.findViewById(R.id.adapterTextAddr);
            adapterEmail = (TextView) v.findViewById(R.id.adapterTextEmail);
            adapterMobile = (TextView) v.findViewById(R.id.adapterTextMobile);
            adapterCity = (TextView) v.findViewById(R.id.adapterTextCity);
            adapterstate = (TextView) v.findViewById(R.id.adapterTextstate);
            adapterstate = (TextView) v.findViewById(R.id.adapterTextstate);
            adapterPin = (TextView) v.findViewById(R.id.adapterTextPin);
            adapterCountry = (TextView) v.findViewById(R.id.adapterTextCountry);
            adapterUserImage = (ImageView) v.findViewById(R.id.adapterUserImage);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_discover_profile_data, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final UserProfileModel Items = discoverItemsArrayList.get(position);


        holder.adapterName.setText(Items.getName());
        holder.adapterAddr.setText(Items.getAddr());
        holder.adapterMobile.setText(Items.getMobile());
        holder.adapterEmail.setText(Items.getEmail());
        holder.adapterCity.setText(Items.getCity());
        holder.adapterstate.setText(Items.getState());
        holder.adapterPin.setText(Items.getPin());
        holder.adapterCountry.setText(Items.getCountry());


        String kept="";
        String str =Items.getImage();
        if(str.contains(","))
            kept = str.substring( 0, str.indexOf(","));
        else
            kept=Items.getImage();
        Picasso.with(activity1).load(CommonUtils.IMAGE_URL+kept).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(holder.adapterUserImage);

    }

    @Override
    public int getItemCount() {
        return discoverItemsArrayList.size();
    }
}
