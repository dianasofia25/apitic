package com.example.apitic.ui.clases;

// Creacion de la clase Tecnicos

public class Tecnicos {

    String Tid;
    String TName;
    String Temail;
    String Tphonenumber;
    String TBirthDate;

    // clic derecho -> generate -> constructor

    public Tecnicos(String tid, String TName, String temail, String tphonenumber, String TBirthDate) {
        this.Tid = tid;
        this.TName = TName;
        this.Temail = temail;
        this.Tphonenumber = tphonenumber;
        this.TBirthDate = TBirthDate;
    }

    // clic derecho -> generate -> getter
    public String getTid() { return Tid; }

    public String getTName() { return TName; }

    public String getTemail() { return Temail; }

    public String getTphonenumber() { return Tphonenumber; }

    public String getTBirthDate() { return TBirthDate; }
}
