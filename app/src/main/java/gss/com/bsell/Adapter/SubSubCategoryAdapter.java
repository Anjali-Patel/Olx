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

import gss.com.bsell.MainActivity;
import gss.com.bsell.Model.CategoriesModel;
import gss.com.bsell.R;
import gss.com.bsell.categories.SubCategory3Activity;
import gss.com.bsell.categories.SubCategoryActivity;

public class SubSubCategoryAdapter extends RecyclerView.Adapter<SubSubCategoryAdapter.MyViewHolder> {
    private Context context1;
    private ArrayList<CategoriesModel> categoriesModels;
    private String catType="";
    private final String sub_cat;
    private final String sub_category_id;
    private final String category;
    private final String category_id;
    private String adType;
    private final String amount;
    private final ArrayList<String> mylist;

    public SubSubCategoryAdapter(Context context, ArrayList<CategoriesModel> categoriesModels, String catType, String sub_cat, String sub_category_id, String category, String category_id, String adType, String amount, ArrayList<String> mylist) {
        context1 = context;
        this.categoriesModels = categoriesModels;
        this.catType = catType;
        this.sub_cat = sub_cat;
        this.sub_category_id = sub_category_id;
        this.category = category;
        this.category_id = category_id;
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
    public SubSubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
        return new SubSubCategoryAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubSubCategoryAdapter.MyViewHolder holder, int position) {
        final CategoriesModel model = categoriesModels.get(position);
        holder.more_textView.setText(model.getCat());
        Picasso.with(context1).load(model.getIcon()).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

        holder.more_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i = new Intent(context1,SubCategory3Activity.class);
                i.putExtra("sub_cat",sub_cat);
                i.putExtra("sub_catid",sub_category_id);
                i.putExtra("category",category);
                i.putExtra("cat_id",category_id);
                i.putExtra("sub_cat2_id",model.getCat_id());
                i.putExtra("sub_cat2",model.getCat());
                i.putExtra("adType",adType);
                i.putExtra("amount",amount);
                i.putExtra("imagelist",mylist);
                ((Activity)context1).startActivity(i);


            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }
}
