package gss.com.bsell.Adapter;

import android.app.Activity;
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

import gss.com.bsell.AddDescriptionActivity;
import gss.com.bsell.MainActivity;
import gss.com.bsell.Model.CategoriesModel;
import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;

public class SubCategory3Adapter extends RecyclerView.Adapter<SubCategory3Adapter.MyViewHolder> {
    private Context context1;
    private ArrayList<CategoriesModel> categoriesModels;
    private String catType="";
    private final String sub_cat2;
    private final String sub_cat_2id;
    private final String sub_cat;
    private final String sub_catid;
    private final String category;
    private final String cat_id;
    private String adType;
    private final String amount;
    private final ArrayList<String> mylist;

    public SubCategory3Adapter(Context context, ArrayList<CategoriesModel> categoriesModels, String catType, String sub_cat2, String sub_cat_2id, String sub_cat, String sub_catid, String category, String cat_id, String adType, String amount, ArrayList<String> mylist) {
        context1 = context;
        this.categoriesModels = categoriesModels;
        this.catType = catType;
        this.sub_cat2 = sub_cat2;
        this.sub_cat_2id = sub_cat_2id;
        this.sub_cat = sub_cat;
        this.sub_catid = sub_catid;
        this.category = category;
        this.cat_id = cat_id;
        this.adType = adType;
        this.amount = amount;
        this.mylist = mylist;
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
    public SubCategory3Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
        return new SubCategory3Adapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategory3Adapter.MyViewHolder holder, int position) {
        final CategoriesModel model = categoriesModels.get(position);
        holder.more_textView.setText(model.getCat());

        if(!model.getIcon().isEmpty()) {
            Picasso.with(context1).load(model.getIcon()).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);
        }

        holder.more_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!CommonUtils.isNullOrEmpty(adType)) {

                    if (adType.equals("buy")) {
                        Intent i = new Intent(context1, MainActivity.class);

                        i.putExtra("sub_cat", sub_cat);
                        i.putExtra("sub_catid", sub_catid);
                        i.putExtra("category", category);
                        i.putExtra("cat_id", cat_id);
                        i.putExtra("sub_cat2_id", sub_cat_2id);
                        i.putExtra("sub_cat2", sub_cat2);
                        i.putExtra("sub_cat3_id", model.getCat_id());
                        i.putExtra("sub_cat3", model.getCat());
                        i.putExtra("addType", adType);
                        i.putExtra("amount",amount);
                        i.putExtra("imagelist",mylist);
                        ((Activity) context1).startActivity(i);

                    } else if (adType.equals("sell")) {
                        Intent i = new Intent(context1, MainActivity.class);

                        i.putExtra("sub_cat", sub_cat);
                        i.putExtra("sub_catid", sub_catid);
                        i.putExtra("category", category);
                        i.putExtra("cat_id", cat_id);
                        i.putExtra("sub_cat2_id", sub_cat_2id);
                        i.putExtra("sub_cat2", sub_cat2);
                        i.putExtra("sub_cat3_id", model.getCat_id());
                        i.putExtra("sub_cat3", model.getCat());
                        i.putExtra("addType", adType);
                        i.putExtra("amount",amount);
                        i.putExtra("imagelist",mylist);
                        ((Activity) context1).startActivity(i);

                    }
                }

//                        i.putExtra("category",model.getCat());

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }
}
