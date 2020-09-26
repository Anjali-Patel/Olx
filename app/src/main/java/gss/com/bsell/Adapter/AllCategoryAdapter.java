package gss.com.bsell.Adapter;

import android.app.Activity;
import android.content.Context;
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
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.R;
import gss.com.bsell.ShowDetailsActivity;
import gss.com.bsell.Utility.CommonUtils;

public class AllCategoryAdapter extends RecyclerView.Adapter<AllCategoryAdapter.MyViewHolder> {
    Context context1;
    private ArrayList<AllCategoryModel> discoverItemsArrayList;

    public AllCategoryAdapter(Context context, ArrayList<AllCategoryModel> discoverItemsModelArrayList) {
        context1 = context;
        discoverItemsArrayList = discoverItemsModelArrayList;
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

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_category_listitems, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AllCategoryModel model = discoverItemsArrayList.get(position);
        holder.adapterTextVal.setText("₹ "+model.getPrice());
        holder.adapterTextDesc.setText(model.getDescription());

        Picasso.with(context1).load(CommonUtils.IMAGE_URL+model.getProduct_image()).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(holder.adapterImage);

        holder.adapterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context1,ShowDetailsActivity.class);
                i.putExtra("image",model.getProduct_image());
                i.putExtra("value",model.getPrice());
                i.putExtra("description",model.getDescription());
                i.putExtra("title",model.getBrand());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context1.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discoverItemsArrayList.size();
    }
}
