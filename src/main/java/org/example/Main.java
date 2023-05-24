package org.example;

import java.util.ArrayList;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws CsvValidationException, FileNotFoundException  {
        ArrayList<Comuna>comunas=new ArrayList<>();
        Comuna comuna = new Comuna();
        Scanner scanner=new Scanner(System.in);
        comuna.leerComunas(comunas);
        for(Comuna comunita: comunas){
            System.out.println("ID: "+comunita.getId()+"\nNombre: "+comunita.getNombreComuna()+"\nCosto: "+comunita.getCosto());
            for(Integer comunitasColindantes: comunita.getComunasColindantes())
                System.out.print(comunitasColindantes + " - ");
            System.out.println();
        }
    }
}