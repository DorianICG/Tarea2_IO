package org.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileNotFoundException;

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
        comuna.generateFirstSolution(solutions,comunas);                                        // PRIMERA SOLUCIÓN

        // INICIO DE LA METAHEURÍSTICA EN EL CICLO
        while(n>0)                                                                              // MIENTRA EL NÚMERO DE ITERACIONES ES MAYOR A 0
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


        /**
         *      CONTAMOS TODAS LAS COMUNAS CON QUE TIENEN UNA ANTENA
         * */
        int contador=0;
        for(Integer solutionInt: bestSolution){
            if(solutionInt==1) contador++;
        }

        /**
         *      IMPRIMIR MENSAJE
         * */
        System.out.print("\n-----------------------------------------------------" +
                "----------------------------------------------------------------" +
                "------------------------------------\n" +
                "LA MEJORES COMUNAS PARA COLOCAR ANTENAS SON: ");
        for(int i=0; i<bestSolution.length; i++)
        {
            if(bestSolution[i]==1 && contador>1)
            {
                System.out.print(comunas.get(i).getNombreComuna()+", ");
                contador--;
            }else if(bestSolution[i]==1 && contador==1){
                System.out.println(comunas.get(i).getNombreComuna()+".");
            }
        }
        DecimalFormat df = new DecimalFormat("0.0");

        System.out.print("\nCON UN COSTO TOTAL DE $"+df.format(comuna.valueCost(bestSolution,comunas))+" MMD\n" +
                "-----------------------------------------------------------------------------------------------" +
                "----------------------------------------------------------");

    }

}