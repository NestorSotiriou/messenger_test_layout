package com.example.messengertestlayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MessengerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MessagesItem> messageItems;
    public static final int SENDER = 0;
    public static final int RECEIVER = 1;

    public static ItemClickListener itemClickListener;
    public interface ItemClickListener{
        void OnClick(int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MessengerAdapter(ArrayList<MessagesItem> messageItems) {
        this.messageItems = messageItems;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;
        if(viewType == SENDER){
            view = layoutInflater.inflate(R.layout.single_sender_message, parent,false);
            return new SenderViewHolder(view);
        }else{
            view = layoutInflater.inflate(R.layout.single_message_reciever,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesItem currentItem = messageItems.get(position);
        if(holder.getItemViewType()==SENDER){
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            senderViewHolder.messageTV.setText(currentItem.getMessage());
            senderViewHolder.messageTimeTV.setText(new SimpleDateFormat("HH:mm").format(new Date(currentItem.getTimestamp())));
        }else{
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            receiverViewHolder.messageTV.setText(currentItem.getMessage());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.OnClick(holder.getAbsoluteAdapterPosition());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }
    public static class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView messageTV;
        TextView messageTimeTV;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.single_messageTV_sender);
            messageTimeTV = itemView.findViewById(R.id.single_messageTV_sender_time);
        }
    }

    public static class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView messageTV;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTV = itemView.findViewById(R.id.single_messageTV_reciever);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageItems.get(position).getSender())
        {
            return SENDER;
        }else {
            return RECEIVER;
        }
    }
}

