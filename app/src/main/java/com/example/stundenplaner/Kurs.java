package com.example.stundenplaner;

public class Kurs {

    private String id;
    private String name;
    private String tag;
    private String raum;
    private String uhrzeit;
    private String status;

    public Kurs(){}
    public Kurs(String id, String name, String tag, String raum, String uhrzeit, String status) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.raum = raum;
        this.uhrzeit = uhrzeit;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public String getRaum() {
        return raum;
    }

    public String getUhrzeit() {
        return uhrzeit;
    }

    public String getStatus() {
        return status;
    }
}
