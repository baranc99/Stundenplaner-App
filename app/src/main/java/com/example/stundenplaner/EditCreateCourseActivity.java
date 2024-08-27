package com.example.stundenplaner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class EditCreateCourseActivity extends AppCompatActivity {
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    public static String addCreateStatus;
    private KursAdapter mAdapter;
    private TextView addCreateText;
    private Button addCreateButton;
    private Button backButton;
    private EditText courseNameInput;
    private EditText courseDayInput;
    private EditText courseRoomInput;
    private EditText courseTimeInput;
    private EditText courseStatusInput;
    String uniqueID;
    Kurs newKurs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_create_course_screen);

        mDatabase = FirebaseDatabase.getInstance("https://stundenplaner-4f226-default-rtdb.europe-west1.firebasedatabase.app//").getReference();

        addCreateText = findViewById(R.id.addCreateText);
        addCreateButton = findViewById(R.id.addCreateBtton);
        backButton = findViewById(R.id.backButton);
        courseNameInput = findViewById(R.id.courseNameInput);
        courseDayInput = findViewById(R.id.courseDayInput);
        courseRoomInput = findViewById(R.id.courseRoomInput);
        courseTimeInput = findViewById(R.id.courseTimeInput);
        courseStatusInput = findViewById(R.id.courseStatusInput);

        uniqueID = UUID.randomUUID().toString();

        if(addCreateStatus.equals("create")){
            addCreateText.setText("Neuen Kurs erstellen");
            addCreateButton.setText("Erstellen");
        }else {
            addCreateText.setText("Kurs bearbeiten");
            addCreateButton.setText("Bearbeiten");
            courseNameInput.setText(mAdapter.chosenCourse.getName());
            courseDayInput.setText(mAdapter.chosenCourse.getTag());
            courseRoomInput.setText(mAdapter.chosenCourse.getRaum());
            courseTimeInput.setText(mAdapter.chosenCourse.getUhrzeit());
            courseStatusInput.setText(mAdapter.chosenCourse.getStatus());
        }

        addCreateButton.setOnClickListener(v -> {
            addCreateCourse();
        });

        backButton.setOnClickListener(v -> {
            updateUI();
        });
    }

    public void addCreateCourse(){
        if(addCreateStatus.equals("create")) {
            newKurs = new Kurs(uniqueID,courseNameInput.getText().toString()
                    ,courseDayInput.getText().toString(),courseRoomInput.getText().toString(),
                    courseTimeInput.getText().toString(),courseStatusInput.getText().toString());
            if(courseNameInput.getText().toString().equals("") || courseDayInput.getText().toString().equals("") || courseRoomInput.getText().toString().equals("") ||
                    courseTimeInput.getText().toString().equals("") ||courseStatusInput.getText().toString().equals("")){
                Toast.makeText(EditCreateCourseActivity.this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
                return;
            }
            mDatabase.child("availableCourses").child(newKurs.getId()).setValue(newKurs)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditCreateCourseActivity.this, "Kurs hinzugefügt", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditCreateCourseActivity.this, AdminActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("firebase", "Succesfull database register");
                        }
                    });
        } else {
            newKurs = new Kurs(mAdapter.chosenCourse.getId(),courseNameInput.getText().toString()
                    ,courseDayInput.getText().toString(),courseRoomInput.getText().toString(),
                    courseTimeInput.getText().toString(),courseStatusInput.getText().toString());
            if(courseNameInput.getText().toString().equals("") || courseDayInput.getText().toString().equals("") || courseRoomInput.getText().toString().equals("") ||
                    courseTimeInput.getText().toString().equals("") ||courseStatusInput.getText().toString().equals("")){
                Toast.makeText(EditCreateCourseActivity.this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
                return;
            }
            mDatabase.child("availableCourses").child(mAdapter.chosenCourse.getId()).setValue(newKurs)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(EditCreateCourseActivity.this, "Kurs bearbeitet", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditCreateCourseActivity.this, AdminActivity.class));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("firebase", "Succesfull database register");
                        }
                    });
        }
    }

    private void updateUI() {
            Intent intent = new Intent(EditCreateCourseActivity.this, AdminActivity.class);
            startActivity(intent);
    }
}
