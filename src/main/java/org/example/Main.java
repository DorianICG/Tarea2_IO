package org.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileNotFoundException;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws CsvValidationException, FileNotFoundException {
        // ARREGLO DE COMUNAS
        ArrayList<Comuna> comunas = new ArrayList<>();

        // CONSTRUCTOR VACÍO PARA INVOCAR FUNCIONES
        Comuna comuna = new Comuna();

        // LEEMOS LAS COMUNAS DEL CSV Y LA GUARDAMOS EL ARREGLO DE COMUNAS
        comuna.leerComunas(comunas);

        // GENERAMOS UNA SOLUCIÓN
        Integer[] bestSolution = null;
        Integer[] antSolution;
        int sampleSizeTotal=100, sampleSizeParents=30, n=100;
        ArrayList<Integer[]>solutions = new ArrayList<>();
        comuna.generateFirstSolution(solutions,comunas);

        while(n>0)
        {
            ArrayList<Integer[]>solutionsNewGenaration = new ArrayList<>();                                             // ARREGLO DE SOLUCIONES NUEVA GEN
            ArrayList<Integer[]>parents = comuna.selectParentsToCross(solutions, comunas, sampleSizeParents);           // SELECCIÓN ELITISTA DE PADRES
            while(solutionsNewGenaration.size()<100)                                                                    // MIENTRAS NO ESTE COMPLETA LA NUEVA GEN
            {
                Integer[] sonSolution = comuna.parentsRandom(parents, comunas);
                comuna.mutateSon(sonSolution, comunas);
                solutionsNewGenaration.add(sonSolution);
            }
            comuna.getBetterSolutions(solutions, solutionsNewGenaration,comunas,sampleSizeTotal);
            if(n==100){
                bestSolution = comuna.getBestSolution(solutions, comunas);
            }else{
                antSolution = comuna.getBestSolution(solutions,comunas);
                if(comuna.valueCost(antSolution,comunas) < comuna.valueCost(bestSolution,comunas))
                {
                    bestSolution=antSolution;
                }
            }
            n--;
        }

        for(int i=0; i<bestSolution.length; i++)
        {
            if(bestSolution[i]==1)
            {
                System.out.print(comunas.get(i).getNombreComuna()+", ");
            }
        }
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.print(df.format(comuna.valueCost(bestSolution,comunas)));

    }

}