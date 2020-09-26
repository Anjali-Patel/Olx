package gss.com.bsell.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gss.com.bsell.ChatActivity;
import gss.com.bsell.MainActivity;
import gss.com.bsell.Model.AllCategoryModel;
import gss.com.bsell.Model.AllChatModel;
import gss.com.bsell.R;
import gss.com.bsell.SelectionScreen;
import gss.com.bsell.ShowDetailsActivity;
import gss.com.bsell.Utility.CommonUtils;
import gss.com.bsell.Utility.SharedPreferenceUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AllChatAdapter extends RecyclerView.Adapter<AllChatAdapter.MyViewHolder> {
    Context context1;
    private ArrayList<AllChatModel> chatItems;
    private String userdemand;
    private SharedPreferenceUtils preferences;

    public AllChatAdapter(Context context, ArrayList<AllChatModel> discoverItemsModelArrayList) {
        context1 = context;
        chatItems = discoverItemsModelArrayList;
        preferences = SharedPreferenceUtils.getInstance(context1);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView chat_username,chat_msg, chat_date, chat_count;
        ImageView chatuser_profileImage;
        LinearLayout l_chat_item_view;
        public MyViewHolder(View v) {
            super(v);
            chat_username = (TextView) v.findViewById(R.id.chat_username);
            chat_msg = (TextView) v.findViewById(R.id.chat_msg);
            chat_date= (TextView) v.findViewById(R.id.chat_date);
            chat_count= (TextView) v.findViewById(R.id.chat_count);
            chatuser_profileImage = (ImageView) v.findViewById(R.id.chatuser_profileImage);
            l_chat_item_view=(LinearLayout)v.findViewById(R.id.l_chat_item_view);

            ((Activity)context1).registerForContextMenu(l_chat_item_view);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.allchat_listitems, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final AllChatModel model = chatItems.get(position);

        if (model.getUserDemand() == null || model.getUserDemand().equalsIgnoreCase("null")  || model.getUserDemand().equalsIgnoreCase("")){
            userdemand = "0";
        }
        else{
            userdemand = model.getUserDemand();
        }

        if(model.getChatCount().equalsIgnoreCase("0")){
            holder.chat_count.setVisibility(View.GONE);
        }
        else{
            holder.chat_count.setText(model.getChatCount());
        }

        holder.chat_msg.setText(model.getMsg());
        holder.chat_date.setText(model.getCreated_date());

        if(model.getReceiver_name().equalsIgnoreCase("null") || model.getReceiver_name() == null){
            holder.chat_username.setText("User");        }
        else{
            holder.chat_username.setText(model.getReceiver_name());        }
//        String kept;
//        String str =model.getImage();
//        String[] arrSplit = str.split(",");
//        kept = CommonUtils.IMAGE_URL+arrSplit[0];
//        if(str.contains(","))
//            kept =  CommonUtils.IMAGE_URL+str.substring( 0, str.indexOf(","));
//        else
//            kept=model.getImage();

        Picasso.with(context1).load(model.getImage()).placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round).into(holder.chatuser_profileImage);



        holder.l_chat_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context1,ChatActivity.class);
                i.putExtra("image",model.getImage());
                i.putExtra("product_id",model.getProduct_id());
                i.putExtra("receiver_id",model.getReceiver_id());
                i.putExtra("category_id",model.getCategory_id());
                i.putExtra("chat_username",model.getReceiver_name());
                i.putExtra("userdemand",userdemand);


                String id = model.getSender_id();
                context1.startActivity(i);
            }
        });

        holder.l_chat_item_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                    MenuItem item1;

                    Context wrapper = new ContextThemeWrapper(context1, R.style.popup);
                    PopupMenu popup = new PopupMenu(wrapper, view);
                    popup.getMenuInflater().inflate(R.menu.delete_chat, popup.getMenu());

                    item1 = popup.getMenu().findItem(R.id.delete);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.delete:

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context1);
                                    builder.create();
                                    builder.setMessage("Are you sure you want to delete this chat?");

                                    builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                        }
                                    });
                                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DeleteChat(model.getProduct_id(),preferences.getStringValue(CommonUtils.USERID, ""),model.getReceiver_id() );
                                        }
                                    });


                                    builder.show();

                                    return true;


                                default:
                                    return false;
                            }

                        }
                    });

                    popup.show();



                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }


    public void DeleteChat(String ProductId, String UserId, String ReceiverId) {

        String url = CommonUtils.APP_URL+"delete_product_chat_by_user";

        OkHttpClient client = new OkHttpClient();


        RequestBody formBody = new FormBody.Builder()
                .add("product_id", ProductId)
                .add("user_id", UserId)
                .add("receiver_id", ReceiverId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody responseBody = response.body();
                final String myResponse = responseBody.string();

                ((Activity)context1).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject json = null;
                        try {
                            json = new JSONObject(myResponse);
                            String success = json.getString("success");

                            if (success.equalsIgnoreCase("1")) {

                                Toast.makeText(context1, "Chat deleted successfully!",
                                        Toast.LENGTH_LONG).show();

                                Intent i = null;
                                i = new Intent(context1, MainActivity.class);
                                i.putExtra("chatFragment","chatFragment");
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                context1.startActivity(i);

                            }

                            else {

                                Toast.makeText(context1, "Opps! Some problem occured while deleting chat, please try again after some time.",
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });




    }
}
