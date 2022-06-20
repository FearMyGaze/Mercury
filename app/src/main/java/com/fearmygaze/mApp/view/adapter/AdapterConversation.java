package com.fearmygaze.mApp.view.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.fearmygaze.mApp.R;
import com.fearmygaze.mApp.model.Conversation;
import com.fearmygaze.mApp.view.activity.ChatRoom;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class AdapterConversation extends RecyclerView.Adapter<AdapterConversation.MyViewHolder> {

    List<Conversation> conversations;
    Activity activity;

    public AdapterConversation(List<Conversation> conversations , Activity activity) {
        this.conversations = conversations;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_conversations, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String id = conversations.get(position).getId();
        String image = conversations.get(position).getImage();
        String username = conversations.get(position).getUsername();
        String lastMessage = conversations.get(position).getLastMessage();
        String time = conversations.get(position).getLastMessageTime();


        Glide.with(holder.itemView.getRootView())
                .load(image)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .apply(RequestOptions.centerCropTransform())
                .into(holder.image);

        holder.username.setText(username);
        holder.lastMessage.setText(lastMessage);
        holder.time.setText(time);

        holder.frameLayout.setOnClickListener(view -> {
            view.getContext().startActivity(new Intent(activity, ChatRoom.class));
            activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            Toast.makeText(view.getContext(), conversations.get(position).getUsername(), Toast.LENGTH_SHORT).show();
        });

        holder.frameLayout.setOnLongClickListener(view -> {
        /*
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext()); //TODO: ADD Theme
            bottomSheetDialog.setContentView(R.layout.dialog_conversation);

            MaterialButton option1 = bottomSheetDialog.findViewById(R.id.dialogConversationOption1);

            option1.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "eixame", Toast.LENGTH_SHORT).show();
            });


            bottomSheetDialog.show();

        */
            Toast.makeText(view.getContext(), "Add a dialog or bottomSheetDialog", Toast.LENGTH_SHORT).show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        FrameLayout frameLayout;
        ShapeableImageView image;
        MaterialTextView username, lastMessage, time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            frameLayout = itemView.findViewById(R.id.adapterConversationRoot);
            image = itemView.findViewById(R.id.adapterConversationImage);
            username = itemView.findViewById(R.id.adapterConversationUsername);
            lastMessage = itemView.findViewById(R.id.adapterConversationLastMessage);
            time = itemView.findViewById(R.id.adapterConversationTime);
        }
    }
}
