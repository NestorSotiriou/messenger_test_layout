package com.example.messengertestlayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuickAnswersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public QuickAnswersAdapter(ArrayList<String> quickMessages) {
        this.quickMessages = quickMessages;
    }

    ArrayList<String> quickMessages;
    public static ItemClickListener itemClickListener;

    public interface ItemClickListener{
        void OnClick(int position);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view;

        view = layoutInflater.inflate(R.layout.single_quick_messages,parent,false);
        return new QuickAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String currentItem = quickMessages.get(position);
        QuickAnswerViewHolder quickAnswerViewHolder = (QuickAnswerViewHolder) holder;
        quickAnswerViewHolder.quickMessageTV.setText(currentItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null)
                    itemClickListener.OnClick(holder.getAbsoluteAdapterPosition());

            }
        });
    }

    @Override
    public int getItemCount() {return quickMessages.size();}

    public static class QuickAnswerViewHolder extends RecyclerView.ViewHolder{
        TextView quickMessageTV;

        public QuickAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            quickMessageTV = itemView.findViewById(R.id.single_quick_messagesTV);
        }
    }
}
