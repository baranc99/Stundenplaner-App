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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StundenplanActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout swipeRefreshLayout;

    private Button btnKursliste;
    private Button btnLogout;
    private Button btnRemoveCourse;

    public static List<Kurs> kursList;
    private KursAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stundenplan_screen);

        recyclerView = findViewById(R.id.recyclerView);
        btnKursliste = findViewById(R.id.btnKursliste);
        btnRemoveCourse = findViewById(R.id.btnRemoveCourse);
        btnLogout = findViewById(R.id.btnLogout);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        swipeRefreshLayout = findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        kursList = new ArrayList<>();
        mAdapter = new KursAdapter(kursList);
        recyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance("https://stundenplaner-4f226-default-rtdb.europe-west1.firebasedatabase.app//").getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();

        btnKursliste.setOnClickListener(v -> {
            Intent intent = new Intent(StundenplanActivity.this, KurslisteActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(StundenplanActivity.this, MainActivity.class);
            startActivity(intent);
        });

        btnRemoveCourse.setOnClickListener(v ->
                removeCourse(mAdapter.chosenCourseId));
    }

    public void getCoursesData() {
        mDatabase.child("studentCourseList").child(currentUser.getUid()).child("courses").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
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

    public void removeCourse(String id){
        kursList.removeIf(s -> s.getId().equals(id));
        mDatabase.child("studentCourseList").child(currentUser.getUid()).child("courses").child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("firebase", "Course deleted successfully");
                Toast.makeText(StundenplanActivity.this, "Kurs entfernt.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("firebase", "Failed to delete course", task.getException());
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        getCoursesData();
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
