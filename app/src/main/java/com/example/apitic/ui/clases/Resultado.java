package com.example.apitic.ui.clases;

public class Resultado {

    String stateE, stateP, stateF, month, year;

    public Resultado(){
    }

    public Resultado(String stateE, String stateP, String stateF, String month, String year) {
        this.stateE = stateE;
        this.stateP = stateP;
        this.stateF = stateF;
        this.month = month;
        this.year = year;
    }

    public String getStateE() { return stateE; }

    public void setStateE(String stateE) { this.stateE = stateE; }

    public String getStateP() { return stateP; }

    public void setStateP(String stateP) { this.stateP = stateP; }

    public String getStateF() { return stateF; }

    public void setStateF(String stateF) { this.stateF = stateF; }

    public String getMonth() { return month; }

    public void setMonth(String month) { this.month = month; }

    public String getYear() { return year; }

    public void setYear(String year) { this.year = year; }
}
