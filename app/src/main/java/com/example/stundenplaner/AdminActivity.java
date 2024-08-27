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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    List<Kurs> kursList;
    SwipeRefreshLayout swipeRefreshLayout;
    private EditCreateCourseActivity aca;
    private KursAdapter mAdapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button deleteCourseButton;
    private Button createCourseButton;
    private Button editCourseButton;
    private Button logoutButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_screen);

        deleteCourseButton = findViewById(R.id.deleteCourseButton);
        createCourseButton = findViewById(R.id.createCourseButton);
        editCourseButton = findViewById(R.id.editCourseButton);
        logoutButton = findViewById(R.id.logoutButton);
        recyclerView = findViewById(R.id.recyclerView2);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://stundenplaner-4f226-default-rtdb.europe-west1.firebasedatabase.app//").getReference();
        kursList = new ArrayList<>();
        mAdapter = new KursAdapter(kursList);
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        getCoursesData();

        deleteCourseButton.setOnClickListener(v ->
                deleteCourse(mAdapter.chosenCourseId));

        createCourseButton.setOnClickListener(v ->
                updateUI("create"));

        editCourseButton.setOnClickListener(v ->
                updateUI("edit"));

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
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
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void deleteCourse(String id) {
        if(mAdapter.chosenCourse == null){
            Toast.makeText(AdminActivity.this, "Bitte wähle zuerst einen Kurs aus", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabase.child("availableCourses").child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("firebase", "Course deleted successfully");
                Toast.makeText(AdminActivity.this, "Kurs entfernt.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("firebase", "Failed to delete course", task.getException());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    private void updateUI(String status) {
            if(status.equals("create")){
                aca.addCreateStatus = "create";
                Intent intent = new Intent(AdminActivity.this, EditCreateCourseActivity.class);
                startActivity(intent);
            } else {
                if(mAdapter.chosenCourse == null){
                    Toast.makeText(AdminActivity.this, "Bitte wähle zuerst einen Kurs aus", Toast.LENGTH_SHORT).show();
                    return;
                }
                    Intent intent = new Intent(AdminActivity.this, EditCreateCourseActivity.class);
                    startActivity(intent);
                    aca.addCreateStatus = "edit";
            }
    }

    @Override
    public void onStart(){
        super.onStart();

       mAdapter.chosenCourse = null;
       mAdapter.chosenCourseId = null;
    }

    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(getIntent());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }
}
