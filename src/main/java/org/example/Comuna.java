package org.example;

import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Comuna {
    private int id;
    private String nombreComuna;
    private float costo;
    private ArrayList<Integer>comunasColindantes = new ArrayList<>();

    public Comuna(int id, String nombreComuna, float costo, ArrayList<Integer> comunasColindantes) {
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

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public ArrayList<Integer> getComunasColindantes() {
        return comunasColindantes;
    }

    public void setComunasColindantes(ArrayList<Integer> comunasColindantes) {
        this.comunasColindantes = comunasColindantes;
    }

    public void leerComunas(ArrayList<Comuna>comunas) throws CsvValidationException, FileNotFoundException {
        try {
            Scanner scan = new Scanner(new File("src/test/java/Comunas.csv"));
            scan.useDelimiter(";;;"); // usar ";;;" como delimitador de línea
            scan.nextLine(); // saltar el encabezado
            while (scan.hasNextLine()) {
                String linea = scan.nextLine();
                linea = linea.replaceAll(";;;", ""); // eliminar el delimitador del final
                String[] datos = linea.split(","); // separar los datos por comas

                    int idNueva = Integer.parseInt(datos[0]); // convertir el primer dato a int
                    String nombreNuevo = datos[1]; // el segundo dato ya es String
                    float costoNuevo = Float.parseFloat(datos[2]); // convertir el tercer dato a float
                    String[] arregloStr = datos[3].split("/"); // separar el arreglo de enteros por barras
                    ArrayList<Integer> arregloNuevo = new ArrayList<Integer>(); // crear un ArrayList de Integer
                    for (String s : arregloStr) {
                        arregloNuevo.add(Integer.parseInt(s)); // convertir cada elemento del arreglo a int y agregarlo al ArrayList
                    }
                    comunas.add(new Comuna(idNueva,nombreNuevo,costoNuevo,arregloNuevo));
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
