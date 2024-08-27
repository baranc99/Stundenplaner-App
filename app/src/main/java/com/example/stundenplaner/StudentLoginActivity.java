package com.example.stundenplaner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentLoginActivity extends AppCompatActivity {

    private EditText etLoginMail, etLoginPassword;
    private Button btnLogin, btnRegister;

    String uniqueID;

    List<Kurs> courselist;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        etLoginMail = findViewById(R.id.etLoginMail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        uniqueID = UUID.randomUUID().toString();
        courselist = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://stundenplaner-4f226-default-rtdb.europe-west1.firebasedatabase.app//").getReference();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btnLogin.setOnClickListener(v ->
                loginStudent());

        btnRegister.setOnClickListener(v ->
                registerUser());

        Button btnBackStudent = findViewById(R.id.btnBackStudent);

        btnBackStudent.setOnClickListener(v -> {
            Intent intent = new Intent(StudentLoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void loginStudent() {
        String email = etLoginMail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(StudentLoginActivity.this, "Füllen Sie alle Felder aus.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals("admin@email.com")) {
            Toast.makeText(StudentLoginActivity.this, "Hier können sich nur Studenten anmelden", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "E-Mail oder Passwort nicht korrekt.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void registerUser() {
        String email = etLoginMail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(StudentLoginActivity.this, "Füllen Sie alle Felder aus.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user != null){
                            writeNewUser(user.getUid(), email, courselist);
                        }
                        updateUI(user);
                    } else {
                        Toast.makeText(StudentLoginActivity.this, "Registration fehlgeschlagen.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    public void writeNewUser(String userId, String email, List<Kurs> courselist) {
        Student student = new Student(email, courselist);

        mDatabase.child("studentCourseList").child(userId).setValue(student)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("firebase", "Succesfull database register");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("firebase", "Succesfull database register");
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(StudentLoginActivity.this, StundenplanActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        if(currentUser != null){
            startActivity(new Intent(StudentLoginActivity.this, StundenplanActivity.class));
        }
    }
}
