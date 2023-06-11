package org.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileNotFoundException;
import java.util.Arrays;

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
        ArrayList<Integer[]>solutions = new ArrayList<>();
        int n = 100;
        comuna.generateFirstSolution(solutions,comunas);
        while(n>0)
        {
            ArrayList<Integer[]>solutionsNewGenaration = new ArrayList<>();

            while(solutionsNewGenaration.size()<50)
            {
                Integer[] fatherSolution = comuna.bestSolution(solutions, comunas);
                Integer[] motherSolution = comuna.secondBestSolution(solutions, comunas, fatherSolution);
                Integer[] sonSolution = comuna.crossParents(fatherSolution, motherSolution, comunas);
                comuna.mutateSon(sonSolution, comunas);
                solutionsNewGenaration.add(sonSolution);

            }
            comuna.getBetterSolutions(solutions, solutionsNewGenaration,comunas);
            if(n==100){
                bestSolution = comuna.getBestSolution(solutions, comunas);
            }else{
                antSolution = comuna.getBestSolution(solutions,comunas);
                if(comuna.valueCost(antSolution,comunas) < comuna.valueCost(bestSolution,comunas))
                {
                    System.out.println("SE MODIFICÓ "+ n );
                    for(int i=0; i<bestSolution.length; i++)
                    {
                        System.out.printf("%5d",bestSolution[i]);
                    }
                    System.out.println();
                    bestSolution=antSolution;
                }
            }


            n--;
        }

        for(int i=0; i<bestSolution.length; i++)
        {
            System.out.printf("%5d",bestSolution[i]);
        }
        System.out.println();
        for(Comuna comunita: comunas)
        {
            System.out.printf("%5d",comunita.getId());
        }
        System.out.println();
        if(comuna.validateSolution(bestSolution,comunas))
        {
            DecimalFormat df = new DecimalFormat("0.00");
            System.out.print("EL PROGRAMA FUNCIONÓ\n");
            System.out.print(df.format(comuna.valueCost(bestSolution,comunas)));
        }


    }

}