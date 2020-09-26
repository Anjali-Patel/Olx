package gss.com.bsell.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gss.com.bsell.AllCategoryActivity;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.R;
import gss.com.bsell.ShowDetailsActivity;
import gss.com.bsell.Utility.CommonUtils;

public class MoreCategoryAdapter extends RecyclerView.Adapter<MoreCategoryAdapter.MyViewHolder> {
    private Context context1;
    private ArrayList<AllCategoryModel> discoverItemsArrayList;

    public MoreCategoryAdapter(Context context, ArrayList<AllCategoryModel> discoverItemsModelArrayList) {
        context1 = context;
        discoverItemsArrayList = discoverItemsModelArrayList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView more_textView;
        public ImageView more_imageView;
        public MyViewHolder(View v) {
            super(v);
            more_textView = (TextView) v.findViewById(R.id.more_textView);
            more_imageView = (ImageView) v.findViewById(R.id.more_imageView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AllCategoryModel model = discoverItemsArrayList.get(position);
        holder.more_textView.setText(model.getDescription());

        String image = model.getImage();

        if (image.equalsIgnoreCase("")){
            Picasso.with(context1).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

        }
        else{
            Picasso.with(context1).load(CommonUtils.CATEGORY_ICON_URL+image).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);
        }


        holder.more_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context1,AllCategoryActivity.class);
                i.putExtra("cat_id",model.getValue());
                i.putExtra("category",model.getDescription());
                context1.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return discoverItemsArrayList.size();
    }
}