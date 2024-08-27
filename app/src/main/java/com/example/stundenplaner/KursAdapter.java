package com.example.stundenplaner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class KursAdapter extends RecyclerView.Adapter<KursAdapter.KursViewHolder> {
    private List<Kurs> mKursList;
    public static Kurs chosenCourse;
    public static String chosenCourseId;

    public static class KursViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textViewName;
        public TextView textViewTag;
        public TextView textViewRaum;
        public TextView textViewUhrzeit;
        public TextView textViewStatus;
        private List<Kurs> mKursList;

        public KursViewHolder(View v, List<Kurs> kursList) {
            super(v);
            textViewName = v.findViewById(R.id.textViewName);
            textViewTag = v.findViewById(R.id.textViewTag);
            textViewRaum = v.findViewById(R.id.textViewRaum);
            textViewUhrzeit = v.findViewById(R.id.textViewUhrzeit);
            textViewStatus = v.findViewById(R.id.textViewStatus);
            mKursList = kursList;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            chosenCourse = mKursList.get(position);
            chosenCourseId = mKursList.get(position).getId();
            Toast.makeText(v.getContext(), "Clicked: " + chosenCourse.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    public KursAdapter(List<Kurs> kursList) {
        mKursList = kursList;
    }

    @NonNull
    @Override
    public KursViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kurs_item, parent, false);
        return new KursViewHolder(v, mKursList);
    }

    @Override
    public void onBindViewHolder(@NonNull KursViewHolder holder, int position) {
        Kurs currentKurs = mKursList.get(position);
        holder.textViewName.setText((currentKurs.getName()));
        holder.textViewTag.setText(currentKurs.getTag());
        holder.textViewRaum.setText(currentKurs.getRaum());
        holder.textViewUhrzeit.setText(currentKurs.getUhrzeit());
        holder.textViewStatus.setText(currentKurs.getStatus());
    }

    @Override
    public int getItemCount() {
        return mKursList.size();
    }
}