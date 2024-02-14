package com.example.timebook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.timebook.Class.Lessons;
import com.example.timebook.Helper.DatabaseHelper;
import com.example.timebook.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class lessonsAdapter extends RecyclerView.Adapter<lessonsAdapter.ViewHolder> {

    private Context context;
    private static OnItemClickListener itemClickListener;
    private List<Lessons> dataList; // List для хранения данных

    public lessonsAdapter(Context context) {
        this.context = context;
        dataList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return dataList.size(); // Возвращаем размер списка данных
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lessons_spisk, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Lessons lessons = dataList.get(position);

        TextView textView = holder.itemView.findViewById(R.id.NameLes);
        textView.setText(lessons.getTitle());

    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void addData(Lessons data) {
        dataList.add(data);
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        Lessons lessons = dataList.get(getAdapterPosition());
                        itemClickListener.onItemClick(lessons.getId());
                    }
                }
            });
        }
    }
}