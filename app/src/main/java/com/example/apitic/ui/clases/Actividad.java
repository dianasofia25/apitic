package com.example.apitic.ui.clases;

import java.util.Date;

public class Actividad {
    private String title, id,finalDate,state,note;
    private Date initialDate;

    public Actividad() {
    }

    public Actividad(String title, String id, Date initialDate,
                     String finalDate, String state, String note) {
        this.title = title;
        this.id = id;
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.state = state;
        this.note = note;;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Date getInitialDate() { return initialDate; }

    public void setInitialDate(Date initialDate) { this.initialDate = initialDate; }

    public String getFinalDate() { return finalDate; }

    public void setFinalDate(String finalDate) { this.finalDate = finalDate; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public String getNote() { return note; }

    public void setNote(String note) { this.note = note; }
}
