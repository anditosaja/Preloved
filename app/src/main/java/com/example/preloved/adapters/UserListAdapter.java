package com.example.preloved.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.preloved.R;
import com.example.preloved.models.UserChatResponse;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {

    private List<UserChatResponse> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserChatResponse user);
    }

    public UserListAdapter(List<UserChatResponse> userList, OnItemClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserChatResponse user = userList.get(position);
        holder.tvNama.setText(user.getNamaLengkap());
        holder.tvPesan.setText(user.getLastMessage() != null ? user.getLastMessage() : "Mulai percakapan...");
        holder.itemView.setOnClickListener(v -> listener.onItemClick(user));
    }

    @Override
    public int getItemCount() { return userList != null ? userList.size() : 0; }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvPesan;
        UserViewHolder(View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNamaUser1);
            tvPesan = itemView.findViewById(R.id.tvPesanTerakhir1);
        }
    }
}
