package com.example.stundenplaner;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText etAdminLoginName;
    EditText etAdminLoginPassword;

    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login_screen);

        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        etAdminLoginName = findViewById(R.id.etAdminLoginName);
        etAdminLoginPassword = findViewById(R.id.etAdminLoginPassword);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnAdminLogin.setOnClickListener(v -> {
            loginAdmin();
        });

        Button btnBackAdmin = findViewById(R.id.btnBackAdmin);

        btnBackAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private void loginAdmin() {
        String email = etAdminLoginName.getText().toString();
        String password = etAdminLoginPassword.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(AdminLoginActivity.this, "Füllen Sie alle Felder aus.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!email.equals("admin@email.com")){
            Toast.makeText(AdminLoginActivity.this, "Nur der Admin kann sich hier anmelden", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(AdminLoginActivity.this, "E-Mail oder Passwort nicht korrekt.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        if(currentUser != null){
            startActivity(new Intent(AdminLoginActivity.this, AdminActivity.class));
        }
    }

}



