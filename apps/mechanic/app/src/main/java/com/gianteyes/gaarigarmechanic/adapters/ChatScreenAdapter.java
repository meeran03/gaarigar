package com.gianteyes.gaarigarmechanic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.ChatMessage;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatScreenAdapter extends RecyclerView.Adapter<com.gianteyes.gaarigarmechanic.adapters.ChatScreenAdapter.Myholder> {
    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPR_RIGHT = 1;
    Context context;
    ArrayList<ChatMessage> list;
    LoginResponseModel user;

    public ChatScreenAdapter(final Context context, final ArrayList<ChatMessage> list, final LoginResponseModel user) {
        this.context = context;
        this.list = list;
        this.user = user;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == ChatScreenAdapter.MSG_TYPE_LEFT) {
            final View view = LayoutInflater.from(this.context).inflate(R.layout.receiver_message_container, parent, false);
            return new Myholder(view);
        } else {
            final View view = LayoutInflater.from(this.context).inflate(R.layout.sender_message_container, parent, false);
            return new Myholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder holder, final int position) {
        final String message = this.list.get(position).getContent();
        holder.message.setText(message);
        holder.message.setText(message);
        holder.message.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return this.list.size();
    }

    public void addMessage(final ChatMessage chatMessage) {
        this.list.add(chatMessage);
    }

    @Override
    public int getItemViewType(final int position) {
        if (this.list.get(position).getSender().equals(this.user.getPhone())) {
            return ChatScreenAdapter.MSG_TYPR_RIGHT;
        } else {
            return ChatScreenAdapter.MSG_TYPE_LEFT;
        }
    }

    class Myholder extends RecyclerView.ViewHolder {

        CircleImageView image;
        TextView message;

        public Myholder(@NonNull final View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.Image);
            this.message = itemView.findViewById(R.id.Message);
        }
    }
}
