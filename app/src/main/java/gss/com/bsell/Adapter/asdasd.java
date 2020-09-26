package gss.com.bsell.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.List;

import gss.com.bsell.Model.DiscoverItemsModel;
import gss.com.bsell.R;
import gss.com.bsell.ShowDetailsActivity;

public class asdasd extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<DiscoverItemsModel> mList;

    public asdasd(Context mContext, List<DiscoverItemsModel> mList) {
        this.mList = mList;
        this.mContext = mContext;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView adapterTextVal,adapterTextDesc;
        ImageView adapterImage;

        public MyViewHolder(View v) {
            super(v);
            adapterTextVal = (TextView) v.findViewById(R.id.adapterTextVal);
            adapterTextDesc = (TextView) v.findViewById(R.id.adapterTextDesc);
            adapterImage = (ImageView) v.findViewById(R.id.adapterImage);
        }
    }

    public static class ViewHolderAdMob extends RecyclerView.ViewHolder {
        public AdView mAdView;
        public ViewHolderAdMob(View view) {
            super(view);
            mAdView = (AdView) view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType){
            case 1:{
                View v = inflater.inflate(R.layout.discover_listitems, parent, false);
                viewHolder = new MyViewHolder(v);
                break;
            }
            case 2:{
                View v = inflater.inflate(R.layout.admob_layout, parent, false);
                viewHolder = new ViewHolderAdMob(v);
                break;
            }
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final DiscoverItemsModel model = mList.get(holder.getAdapterPosition());

        switch(holder.getAdapterPosition()){
            case 1:{
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.adapterTextVal.setText(model.getValue());
                viewHolder.adapterTextDesc.setText(model.getDescription());

                Picasso.with(mContext).load(model.getImage()).placeholder(R.mipmap.ic_launcher_round)
                        .error(R.mipmap.ic_launcher_round).into(viewHolder.adapterImage);

                viewHolder.adapterImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(mContext,ShowDetailsActivity.class);
                        i.putExtra("image",model.getImage());
                        mContext.startActivity(i);
                    }
                });
                break;
            }
            case 2:{
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}