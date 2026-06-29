package com.example.preloved.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloved.R;
import com.example.preloved.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatList;
    private int myUserId;

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatList, int myUserId) {
        this.chatList = chatList;
        this.myUserId = myUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Jika senderId dari model pesan sama dengan myUserId, maka ini pesan kita (SENT)
        if (chatList.get(position).getSenderId() == myUserId) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatList.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            sentHolder.tvIsiPesan.setText(message.getMessage());
            sentHolder.tvWaktu.setText(message.getTime() != null ? message.getTime() : "");
        } else {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            receivedHolder.tvIsiPesan.setText(message.getMessage());
            receivedHolder.tvWaktu.setText(message.getTime() != null ? message.getTime() : "");
        }
    }

    @Override
    public int getItemCount() {
        return (chatList != null) ? chatList.size() : 0;
    }

    // ViewHolder Kanan
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvIsiPesan, tvWaktu;
        SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIsiPesan = itemView.findViewById(R.id.tvIsiPesanKanan);
            tvWaktu = itemView.findViewById(R.id.tvWaktuKanan);
        }
    }

    // ViewHolder Kiri
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvIsiPesan, tvWaktu;
        ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIsiPesan = itemView.findViewById(R.id.tvIsiPesanKiri);
            tvWaktu = itemView.findViewById(R.id.tvWaktuKiri);
        }
    }
}
