package gss.com.bsell;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import gss.com.bsell.Model.MyChatModel;

public class InChatAdapter extends RecyclerView.Adapter<InChatAdapter.MyViewHolder> {
    private ArrayList<MyChatModel> ChatArrayList;
    private Context context;
    private String User_id;

    public InChatAdapter(Context applicationContext, ArrayList<MyChatModel> advertiseArray, String user_id) {
        this.context = applicationContext;
        this.ChatArrayList = advertiseArray;
        User_id = user_id;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView singleText, chat_time;
        LinearLayout mainchatlayout;

        public MyViewHolder(View v) {
            super(v);
            singleText = v.findViewById(R.id.singleText);
            chat_time = v.findViewById(R.id.dateNtime);
            mainchatlayout = v.findViewById(R.id.mainchatlayout);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_listitems, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.chat_time.setText(ChatArrayList.get(position).getCreated_date());
        holder.singleText.setText(ChatArrayList.get(position).getMessage());

        if (ChatArrayList.get(position).getSender_id().equals(User_id)) {
            //Mychats
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            holder.mainchatlayout.setOrientation(LinearLayout.VERTICAL);
            layoutParams2.gravity = Gravity.END;
            layoutParams2.setMargins(60, 0, 0, 0);
            holder.mainchatlayout.setLayoutParams(layoutParams2);
            holder.singleText.setGravity(Gravity.START);
            holder.mainchatlayout.setBackgroundColor(context.getResources().getColor( R.color.user_chat_bg));
        } else if (!ChatArrayList.get(position).getSender_id().equals(User_id)) {
            //OtherPerson
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            holder.mainchatlayout.setOrientation(LinearLayout.VERTICAL);
            layoutParams.gravity = Gravity.START;
            layoutParams.setMargins(0, 0, 90, 0);
            holder.mainchatlayout.setLayoutParams(layoutParams);
            holder.singleText.setGravity(Gravity.START);
            holder.mainchatlayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return ChatArrayList.size();
    }
}