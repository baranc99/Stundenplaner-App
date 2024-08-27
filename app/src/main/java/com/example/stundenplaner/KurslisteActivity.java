package com.example.stundenplaner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KurslisteActivity extends AppCompatActivity{
    DatabaseReference mDatabase;
    FirebaseUser currentUser;

    List<Kurs> kursList;
    List<Kurs> filteredList;
    private RecyclerView recyclerView;
    private KursAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Button addCourseButton;

    public StundenplanActivity sp;

    String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kursliste_screen);

        mDatabase = FirebaseDatabase.getInstance("https://stundenplaner-4f226-default-rtdb.europe-west1.firebasedatabase.app//").getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        addCourseButton = findViewById(R.id.btnAddCourse);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        kursList = new ArrayList<>();
        filteredList = new ArrayList<>();
        mAdapter = new KursAdapter(filteredList);
        recyclerView.setAdapter(mAdapter);
        uniqueID = UUID.randomUUID().toString();

        addCourseButton.setOnClickListener(v ->
                addCourse(mAdapter.chosenCourseId,mAdapter.chosenCourse));

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(KurslisteActivity.this, StundenplanActivity.class);
            startActivity(intent);
        });

    }

    public void getCoursesData() {
        mDatabase.child("availableCourses").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data ", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    for (DataSnapshot element : dataSnapshot.getChildren()) {
                        Kurs k = element.getValue(Kurs.class);
                        k.setId(element.getKey());
                        kursList.add(k);

                    }
                    filterCourseList();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void addCourse(String id, Kurs kurs){
        if(mAdapter.chosenCourse == null) {
            Toast.makeText(KurslisteActivity.this, "Wähle zunächst einen Kurs zum entfernen", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("studentCourseList").child(currentUser.getUid()).child("courses").child(id).setValue(kurs).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("firebase", "Course added successfully");
                Toast.makeText(KurslisteActivity.this, "Kurs hinzugefügt.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(KurslisteActivity.this, StundenplanActivity.class));
            } else {
                Log.e("firebase", "Failed to add course", task.getException());
            }
        });
    }

    public void filterCourseList() {
        List<String> courseNames = new ArrayList<>();
        for (Kurs kurs : sp.kursList) {
            courseNames.add(kurs.getName());
        }
        filteredList.addAll(kursList.stream()
                .filter(kurs -> !courseNames.contains(kurs.getName()))
                .collect(Collectors.toList()));
        for (Kurs kurs : filteredList) {
            Log.e("firebase", kurs.getId());
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        getCoursesData();
    }
}
