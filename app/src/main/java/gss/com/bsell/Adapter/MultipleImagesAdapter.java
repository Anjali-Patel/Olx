package gss.com.bsell.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import gss.com.bsell.R;
//
//public class MultipleImagesAdapter extends RecyclerView.Adapter<MultipleImagesAdapter.MyViewHolder> {
//    Context context1;
//    private ArrayList<String> MultipleImagesList;
//
//    public MultipleImagesAdapter(Context context, ArrayList<String> ImagesList) {
//        context1 = context;
//        MultipleImagesList = ImagesList;
//    }
//
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder {
//        ImageView adapterImage;
//
//        public MyViewHolder(View v) {
//            super(v);
//            adapterImage = (ImageView) v.findViewById(R.id.adapterImageview);
//        }
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        // create a new view
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_image_listitem, parent, false);
//
//        return new MyViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//
//        Picasso.with(context1).load(MultipleImagesList.get(position)).placeholder(R.mipmap.ic_launcher_round)
//                .error(R.mipmap.ic_launcher_round).into(holder.adapterImage);
//        }
//
//    @Override
//    public int getItemCount() {
//        return MultipleImagesList.size();
//    }
//}
