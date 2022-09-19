package com.example.apitic.ui.clases;

import java.util.Date;

public class Respuesta {
    private Date dateR;
    private String descriptionR,userR;

    public Respuesta() {
    }

    public Respuesta(Date dateR, String descriptionR, String userR) {
        this.dateR = dateR;
        this.descriptionR = descriptionR;
        this.userR = userR;
    }

    public Date getDateR() {
        return dateR;
    }

    public void setDateR(Date dateR) {
        this.dateR = dateR;
    }

    public String getDescriptionR() {
        return descriptionR;
    }

    public void setDescriptionR(String descriptionR) {
        this.descriptionR = descriptionR;
    }

    public String getUserR() {
        return userR;
    }

    public void setUserR(String userR) {
        this.userR = userR;
    }
}
