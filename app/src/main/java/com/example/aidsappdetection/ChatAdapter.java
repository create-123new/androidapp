package com.example.aidsappdetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> chatList;

    public ChatAdapter(List<Message> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = chatList.get(position);

        if (message.isUser()) {
            holder.userMessage.setText(message.getText());
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.botMessage.setVisibility(View.GONE);
        } else {
            holder.botMessage.setText(message.getText());
            holder.botMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userMessage, botMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.userMessage);
            botMessage = itemView.findViewById(R.id.botMessage);
        }
    }
}
