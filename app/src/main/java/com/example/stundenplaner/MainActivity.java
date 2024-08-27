package com.example.stundenplaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_screen);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        btnStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StudentLoginActivity.class);
            startActivity(intent);
        });

       btnAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminLoginActivity.class);
            startActivity(intent);
       });
    }

    @Override
    public void onStart(){
        super.onStart();

        if(currentUser != null) {
            if(currentUser.getEmail().toString().equals("admin@email.com")){
                startActivity(new Intent(MainActivity.this, AdminActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, StundenplanActivity.class));
            }
        }
    }
}