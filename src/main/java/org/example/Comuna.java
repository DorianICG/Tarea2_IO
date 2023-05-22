package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class Comuna {
    private int id;
    private String nombreComuna;
    private int costo;
    private ArrayList<Comuna>comunasColindantes = new ArrayList<>();

    public Comuna(int id, String nombreComuna, int costo, ArrayList<Comuna> comunasColindantes) {
        this.id = id;
        this.nombreComuna = nombreComuna;
        this.costo = costo;
        this.comunasColindantes = comunasColindantes;
    }

    public Comuna() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreComuna() {
        return nombreComuna;
    }

    public void setNombreComuna(String nombreComuna) {
        this.nombreComuna = nombreComuna;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public ArrayList<Comuna> getComunasColindantes() {
        return comunasColindantes;
    }

    public void setComunasColindantes(ArrayList<Comuna> comunasColindantes) {
        this.comunasColindantes = comunasColindantes;
    }

}
