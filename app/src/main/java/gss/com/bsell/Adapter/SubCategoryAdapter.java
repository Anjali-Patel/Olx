package gss.com.bsell.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import gss.com.bsell.Model.SelectedCategoryDataModel;
import gss.com.bsell.R;
import gss.com.bsell.SellActivity;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import gss.com.bsell.categories.SubCategory2Activity;
import gss.com.bsell.categories.SubCategoryActivity;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {
private Context context1;
private ArrayList<CategoriesModel> categoriesModels;
String editCategoryOperated;
String SelectedPosition;
String adType;

    SharedPreferences sharedpreferences;
    private SharedPreferenceUtils preferences;


    public SubCategoryAdapter(Context context, ArrayList<CategoriesModel> categoriesModels, String editCategoryOperated, String position, String adType) {
        context1 = context;
        this.categoriesModels = categoriesModels;
        this.editCategoryOperated = editCategoryOperated;
        this.SelectedPosition = position;
        this.adType = adType;

        preferences = SharedPreferenceUtils.getInstance(context);


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
    public SubCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.more_data_listitems, parent, false);
        return new SubCategoryAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryAdapter.MyViewHolder holder, final int position) {

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

        }
        holder.more_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (editCategoryOperated.equalsIgnoreCase("yes")){

                    // int position = Integer.parseInt(preferences.getStringValue(CommonUtils.POSITIONOFCAT,""));


                    CommonUtils.SELECTEDCATEGORY.remove(Integer.parseInt(SelectedPosition));

                    ArrayList<SelectedCategoryDataModel> seletedData = new ArrayList<SelectedCategoryDataModel>();

                    SelectedCategoryDataModel selectedCatModel = new SelectedCategoryDataModel();
                    selectedCatModel.setCatId(model.getCat_id());
                    selectedCatModel.setCatType(model.getAttribute());
                    selectedCatModel.setCatData(model.getCat());
                    selectedCatModel.setPreviousCategoryId(model.getCurrent_Cat_id());
                    CommonUtils.SELECTEDCATEGORY.add(Integer.parseInt(SelectedPosition),selectedCatModel);

                    Intent i = new Intent(context1,SubCategoryActivity.class);
                    i.putExtra("category_id",model.getCat_id());
                    i.putExtra("category",model.getCat());
                    i.putExtra("attribute_name",model.getAttribute());
                    i.putExtra("adType",adType);
                    i.putExtra("editCategoryOperated","done");
                    i.putExtra("position","No_position");

                    ((Activity)context1).startActivity(i);

                }

                else {


                    ArrayList<SelectedCategoryDataModel> seletedData = new ArrayList<SelectedCategoryDataModel>();

                    SelectedCategoryDataModel selectedCatModel = new SelectedCategoryDataModel();
                    selectedCatModel.setCatId(model.getCat_id());
                    selectedCatModel.setCatType(model.getAttribute());
                    selectedCatModel.setCatData(model.getCat());
                    selectedCatModel.setPreviousCategoryId(model.getCurrent_Cat_id());

                    for (int i = CommonUtils.SELECTEDCATEGORY.size()-1; i >=0 ; i--) {
                        if (CommonUtils.SELECTEDCATEGORY.get(i).getPreviousCategoryId().equalsIgnoreCase(model.getCurrent_Cat_id())) {
                            CommonUtils.SELECTEDCATEGORY.remove(i);
                        }

                    }


                    CommonUtils.SELECTEDCATEGORY.add(selectedCatModel);

                    Intent i = new Intent(context1,SubCategoryActivity.class);
                    i.putExtra("category_id",model.getCat_id());
                    i.putExtra("category",model.getCat());
                    i.putExtra("attribute_name",model.getAttribute());
                    i.putExtra("adType",adType);
                    i.putExtra("editCategoryOperated","no");
                    i.putExtra("position","No_position");


                    ((Activity)context1).startActivity(i);
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }
}
