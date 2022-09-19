package com.example.apitic.ui.clases;

import java.util.ArrayList;
import java.util.Date;

public class Pregunta {
    private String userP, title, description,dateE,categoria;
    private Date dateP;

    public Pregunta() {
    }

    public Pregunta(String userP, Date dateP, String dateE, String title, String description, String categoria) {
        this.userP = userP;
        this.dateP = dateP;
        this.dateE = dateE;
        this.title = title;
        this.description = description;
        this.categoria = categoria;
    }

    public String getUserP() {
        return userP;
    }

    public void setUserP(String userP) {
        this.userP = userP;
    }

    public Date getDateP() {
        return dateP;
    }

    public void setDate(Date dateP) {
        this.dateP = dateP;
    }

    public String getDateE() { return dateE; }

    public void setDateE(String dateE) { this.dateE = dateE; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }
}
