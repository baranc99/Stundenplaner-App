package com.example.stundenplaner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Student {
    public String email;

    public List<Kurs> courselist = new ArrayList<>();

    public Student() {

    }

    public Student( String email, List<Kurs> courselist) {
        this.email = email;
        this.courselist = courselist;
    }
}
