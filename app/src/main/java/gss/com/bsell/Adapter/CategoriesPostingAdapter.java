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
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.categories.SubCategoryActivity;

public class CategoriesPostingAdapter extends RecyclerView.Adapter<CategoriesPostingAdapter.MyViewHolder> {
    private Context context1;
    private ArrayList<CategoriesModel> categoriesModels;
    private String catType="";
    private String adType;
    private final String amount;
    private final ArrayList<String> mylist;
    SharedPreferenceUtils preferances;



    public CategoriesPostingAdapter(Context context, ArrayList<CategoriesModel> categoriesModels, String catType, String adType, String amount, ArrayList<String> mylist) {
        context1 = context;
        this.categoriesModels = categoriesModels;
        this.catType = catType;

        this.adType = adType;
        this.amount = amount;
        this.mylist = mylist;

        preferances=SharedPreferenceUtils.getInstance(context1);

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
    public CategoriesPostingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
        return new CategoriesPostingAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesPostingAdapter.MyViewHolder holder, int position) {
        final CategoriesModel model = categoriesModels.get(position);

        holder.more_textView.setText(model.getCat());

        String image = model.getIcon();

        if (image.equalsIgnoreCase("")){
            Picasso.with(context1).load(R.mipmap.ic_launcher_round).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

        }
        else{
            Picasso.with(context1).load(CommonUtils.CATEGORY_ICON_URL+image).placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);

//            Picasso.with(context1).load(model.getIcon()).placeholder(R.mipmap.ic_launcher_round)
//             .error(R.mipmap.ic_launcher_round).into(holder.more_imageView);
        }


        holder.more_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(context1,SubCategoryActivity.class);
                i.putExtra("category_id",model.getCat_id());
                i.putExtra("category",model.getCat());
                i.putExtra("attribute_name",model.getAttribute());
                i.putExtra("editCategoryOperated","0");
                i.putExtra("adType",adType);
                i.putExtra("amount",amount);
                i.putExtra("imagelist",mylist);

                String id = model.getCat_id();
                preferances.setValue(CommonUtils.MAINCATEGORY, id);
                preferances.setValue(CommonUtils.MAINCATEGORYNAME, model.getCat());


                ((Activity)context1).startActivity(i);

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }
}
