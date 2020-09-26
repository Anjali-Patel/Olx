package gss.com.bsell.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import gss.com.bsell.Model.SelectedCategoryDataModel;
import gss.com.bsell.R;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.categories.SubCategoryActivity;

public class SelectedCategoryAdapter extends RecyclerView.Adapter<SelectedCategoryAdapter.MyViewHolder>  {

    Context context;
    private ArrayList<SelectedCategoryDataModel> CategoriesDetailsArrayList;
    String adType;

    SharedPreferences sharedpreferences;
    private SharedPreferenceUtils preferences;

    public SelectedCategoryAdapter(Context context,  ArrayList<SelectedCategoryDataModel> CategoriesDetailsModelArrayList, String adType)
    {
        this.context=context;
        CategoriesDetailsArrayList = CategoriesDetailsModelArrayList;
        this.adType = adType;

        preferences = SharedPreferenceUtils.getInstance(context);

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCatName, tvCatVal;
        ImageView EditCatButton;
        //View divider;
        RelativeLayout EditCat;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCatVal = itemView.findViewById(R.id.tvCatVal);
            tvCatName = itemView.findViewById(R.id.tvCatName);
            EditCatButton = itemView.findViewById(R.id.EditCatButton);
            EditCat= itemView.findViewById(R.id.EditCat);
            //divider = itemView.findViewById(R.id.divider);
        }
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {

        final SelectedCategoryDataModel Items = CategoriesDetailsArrayList.get(position);

        holder.tvCatName.setText(Items.getCatType());
        holder.tvCatVal.setText(Items.getCatData());

        holder.EditCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(context,SubCategoryActivity.class);

                i.putExtra("category_id",Items.getPreviousCategoryId());
                i.putExtra("category",Items.getCatType());
                i.putExtra("attribute_name",Items.getCatData());
                i.putExtra("adType",adType);
                i.putExtra("editCategoryOperated","yes");
                i.putExtra("position",String.valueOf(position));


                ((Activity)context).startActivity(i);

            }
        });

        holder.EditCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context,SubCategoryActivity.class);

                i.putExtra("category_id",Items.getPreviousCategoryId());
                i.putExtra("category",Items.getCatType());
                i.putExtra("attribute_name",Items.getCatData());
                i.putExtra("adType",adType);
                i.putExtra("editCategoryOperated","yes");
                i.putExtra("position",String.valueOf(position));


                ((Activity)context).startActivity(i);

            }
        });

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewgroup, final int position)
    {

        View v = LayoutInflater.from(context).inflate(R.layout.cat_display_listitem, viewgroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;

    }

    @Override
    public int getItemCount()
    {

        return CategoriesDetailsArrayList.size();
    }


}
