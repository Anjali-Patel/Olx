package gss.com.bsell.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gss.com.bsell.Model.DiscoverItemsModel;
import gss.com.bsell.R;
import gss.com.bsell.ShowDetailsActivity;
import gss.com.bsell.Utility.CommonUtils;

public class DiscoverItemsAdapter extends RecyclerView.Adapter<DiscoverItemsAdapter.MyViewHolder> {
    private Activity activity1;
    private ArrayList<DiscoverItemsModel> discoverItemsArrayList;
    private String userdemand = "0";
    String DeleteProduct;


    public DiscoverItemsAdapter(FragmentActivity activity, ArrayList<DiscoverItemsModel> discoverItemsModelArrayList, String userdemand, String IsDelete) {
        activity1 = activity;
        discoverItemsArrayList = discoverItemsModelArrayList;
        DeleteProduct = IsDelete;
        //this.userdemand = userdemand;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView adapterTextVal, adapterTextDesc,adapterTextVal2, Km;
        ImageView adapterImage, productIcon;

        public MyViewHolder(View v) {
            super(v);
            adapterTextVal = (TextView) v.findViewById(R.id.adapterTextVal);
            adapterTextDesc = (TextView) v.findViewById(R.id.adapterTextDesc);
            adapterImage = (ImageView) v.findViewById(R.id.adapterImage);
            adapterTextVal2 = (TextView) v.findViewById(R.id.adapterTextVal2);
            productIcon = (ImageView) v.findViewById(R.id.productIcon);
            Km = (TextView) v.findViewById(R.id.KM);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_listitems, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final DiscoverItemsModel Items = discoverItemsArrayList.get(position);

        String IconBase = "http://139.59.15.90/bsell/assets/upload/categoryIcon/";


        Picasso.with(activity1).load(IconBase+Items.getProductCatIcon()).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(holder.productIcon);

        holder.adapterTextVal.setText("₹"+Items.getValue());


        holder.adapterTextDesc.setText(Items.getDescription());
        //holder.adapterTextVal2.setText(Items.getTitle());
        holder.adapterTextVal2.setText(Items.getTitle());

        String kept="";
        String str =Items.getImage();
        if(str.contains(","))
            kept = str.substring( 0, str.indexOf(","));
        else
            kept=Items.getImage();
//        String remainder = str.substring(str.indexOf(",")+1, str.length());


//        Picasso.with(activity1).load(CommonUtils.IMAGE_URL+Items.getImage()).placeholder(R.mipmap.ic_launcher_round)
//                .error(R.mipmap.ic_launcher_round).into(holder.adapterImage);
//
        Picasso.with(activity1).load(CommonUtils.IMAGE_URL+kept).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)/*.memoryPolicy(MemoryPolicy.NO_CACHE).networkPolicy(NetworkPolicy.)*/.into(holder.adapterImage);

        final String finalKept = kept;
        holder.adapterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity1, ShowDetailsActivity.class);
                //i.putExtra("image", Items.getImage());
                i.putExtra("id",Items.getProductId());
                i.putExtra("userdemand",Items.getUserDemand());
                i.putExtra("userid",Items.getProductSenderId());
                i.putExtra("category_id",Items.getCatagoryId());
                i.putExtra("mobile",Items.getMobile());
                i.putExtra("sm_productId",Items.getSimilarProductId());
                i.putExtra("image",CommonUtils.IMAGE_URL+ finalKept);
                i.putExtra("chat_username",Items.getUsername());
                i.putExtra("IsDelete",DeleteProduct);




                String userid = Items.getProductSenderId();
                //        Log.d("Cat", userid);
                activity1.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discoverItemsArrayList.size();
    }
}
