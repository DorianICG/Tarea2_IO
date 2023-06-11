package org.example;

import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
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

    public void generateFirstSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas)
    {
        while(solutions.size()<100)
        {
            Integer[]solution = new Integer[comunas.size()];
            solutions.add(newSolution(solution, comunas));
        }
    }

    public Integer[] bestSolution(ArrayList<Integer[]>solutions,ArrayList<Comuna>comunas)
    {
        Integer[] firstSolution = null;
        double firstCoste = 0;
        int i=0;
        for(Integer[]solution: solutions)
        {
            if(i==0)
            {
                firstSolution = solution;
                firstCoste = valueCost(solution, comunas);
                i++;
            } else if (valueCost(solution, comunas)<firstCoste) {
                firstSolution = solution;
                firstCoste = valueCost(solution, comunas);
            }
        }
        return firstSolution;
    }

    public Integer[] secondBestSolution(ArrayList<Integer[]>solutions,ArrayList<Comuna>comunas, Integer[]firstsolution)
    {
        Integer[] secondSolution = null;
        double secondCost = 0;
        int i=0;
        for(Integer[]solution: solutions)
        {
            if(!firstsolution.equals(solution))
            {
                if(i==0)
                {
                    secondSolution = solution;
                    secondCost = valueCost(solution, comunas);
                    i++;
                } else if (valueCost(solution, comunas)<secondCost && i>0) {
                    secondSolution = solution;
                    secondCost = valueCost(solution, comunas);
                }
            }

        }
        return secondSolution;
    }

    public Integer[] crossParents(Integer[] father, Integer[] mother, ArrayList<Comuna>comunas)
    {
        ArrayList<Integer[]>sons = new ArrayList<>(10);
        int random;
        int i=0;
        while(i<10)
        {
            for(int parents=0;parents<2;parents++)
            {
                random = (int)(Math.random()*(father.length - 1)+1);
                Integer[]son= new Integer[father.length];

                for(int posParent1=0;posParent1<random;posParent1++)
                {
                    if(parents==0)
                    {
                        son[posParent1]=father[posParent1];
                    }else
                    {
                        son[posParent1]=mother[posParent1];
                    }

                }
                for(int posParent2=random;posParent2<father.length;posParent2++) {
                    if (parents == 0) {
                        son[posParent2] = mother[posParent2];
                    } else {
                        son[posParent2] = father[posParent2];
                    }
                }
                sons.add(son);
            }
            i++;
        }


        return bestSolution(sons,comunas);
    }

    public void mutateSon(Integer[]son, ArrayList<Comuna>comunas)
    {
        validSolution(son, comunas);
    }

    public void getBetterSolutions(ArrayList<Integer[]>solutions,ArrayList<Integer[]>solutionsNewGenaration,ArrayList<Comuna>comunas)
    {
        solutions.addAll(solutionsNewGenaration);
        bubbleSortInteger(solutions, comunas);
        solutions.subList(50,solutions.size()).clear();
    }

    public void bubbleSortInteger(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas)
    {
        for(int i=0; i<solutions.size()-1;i++)
        {
            for(int j=0; j<solutions.size()-1-i;j++)
            {
                if(valueCost(solutions.get(j),comunas)>valueCost(solutions.get(j+1), comunas))
                {
                    Integer[]swap = solutions.get(j);
                    solutions.set(j, solutions.get(j+1));
                    solutions.set(j+1, swap);
                }
            }
        }
    }

    public Integer[] getBestSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas)
    {
        Integer[] bestSolutionArray =null;
        double bestCost=0;
        for(int i=0;i<solutions.size(); i++)
        {
            if(i==0)
            {
                bestSolutionArray = solutions.get(i);
                bestCost = valueCost(solutions.get(i), comunas);
            }else if(valueCost(solutions.get(i), comunas)<bestCost)
            {
                bestSolutionArray = solutions.get(i);
                bestCost = valueCost(solutions.get(i), comunas);
            }
        }
        return bestSolutionArray;
    }

    public Integer[] newSolution(Integer[]solution, ArrayList<Comuna>comunas){
        Random rn = new Random();
        double alfa = 0.3;
        for (int i = 0; i < solution.length; i++)
        {
            if(rn.nextDouble()<alfa){
                solution[i] = 1;
            }else{
                solution[i] = 0;
            }
        }
        validSolution(solution,comunas);
        return solution;
    }


    public void validSolution(Integer[]solution, ArrayList<Comuna>comunas) {
        int cont, mejorPos = -1;
        Random rn = new Random();
        double mejorCoste, costeNuev = -1;
        boolean flag;
            for (int i = 0; i < comunas.size(); i++)                                      //recorro todas las comunas
            {
                Comuna currentCommune = comunas.get(i);                             // tomo la comuna actual
                if (solution[1] == 1) {                                                 // valido si en la posición hay antena
                    flag = false;
                    mejorCoste = 99999;
                    mejorPos = -1;
                    for (int j = 0; j < currentCommune.comunasColindantes.size(); j++)  // voy recorriendo el array de comunas vecinas
                    {
                        for (int k = 0; k < comunas.size(); k++) {
                            if (comunas.get(k).id == currentCommune.comunasColindantes.get(j)) {
                                if (solution[k] == 1) flag = true;
                                break;
                            }
                        }
                        if (flag) break;
                    }
                    while (!flag) {
                        mejorPos = rn.nextInt(currentCommune.comunasColindantes.size());
                        for (int j = 0; j < currentCommune.comunasColindantes.size(); j++)  // voy recorriendo el array de comunas vecinas
                        {
                            for (int k = 0; k < comunas.size(); k++)                          // recorro todo el arreglo comunas para encontrar el la comuna vecina
                            {
                                if (comunas.get(k).id == currentCommune.comunasColindantes.get(j)) //  verifico si la id de la comuna es igual a la la id de las comunas vecinas
                                {
                                    Comuna neighbor = comunas.get(k);
                                    for (int y = 0; y < neighbor.comunasColindantes.size(); y++)      //  recorro toda la colección de de vecinos de la comuna vecina
                                    {

                                        for (int h = 0; h < comunas.size(); h++)                      // recorro comunas para ver la id del vecino del vecino
                                        {
                                            if (comunas.get(h).id == neighbor.comunasColindantes.get(y))   // si el coincide la id del vecino
                                            {
                                                if (!comunas.get(h).equals(comunas.get(k)))             // valido si el vecino del vecino no es mi comuna que estoy recorriendo
                                                {
                                                    if (solution[h] == 1)                                  // si tiene un 1 alguna de sus vecinos
                                                    {
                                                        flag = true;                                      // activo la bandera de que si tiene al menos un vecino con antena
                                                        costeNuev = comunas.get(h).costo / comunas.get(h).comunasColindantes.size();  // saco el coste del vecino
                                                        if (costeNuev < mejorCoste)                                                    // comparo con el mejor coste
                                                        {
                                                            mejorCoste = costeNuev;                                                   // remplazo
                                                            mejorPos = h;                                                             // remplazo
                                                        }
                                                    }
                                                    break;
                                                }
                                            }

                                        }
                                        if (flag) break;
                                    }

                                }
                            }

                        }
                        if (!flag) {
                            solution[rn.nextInt(currentCommune.comunasColindantes.size())] = 1;
                            flag = true;
                        } else {
                            solution[mejorPos] = 1;
                        }

                    }
                } else {
                    cont = 0;
                    for (int j = 0; j < currentCommune.comunasColindantes.size(); j++) {
                        for (int k = 0; k < comunas.size(); k++) {
                            if (comunas.get(k).id == currentCommune.comunasColindantes.get(j)) {
                                if (solution[k] == 1) {
                                    cont++;
                                }
                                break;
                            }
                        }
                    }
                    if (cont == 0) {
                        mejorCoste = comunas.get(i).costo / comunas.get(i).comunasColindantes.size();
                        mejorPos = i;
                        for (int j = 0; j < currentCommune.comunasColindantes.size(); j++) {
                            for (int k = 0; k < comunas.size(); k++) {
                                if (currentCommune.comunasColindantes.get(j) == comunas.get(k).id) {
                                    costeNuev = comunas.get(k).costo / comunas.get(k).comunasColindantes.size();
                                    if (costeNuev < mejorCoste) {
                                        mejorCoste = costeNuev;
                                        mejorPos = k;
                                    }
                                }
                            }
                        }
                        solution[mejorPos] = 1;
                    } else if (cont == 1 && currentCommune.comunasColindantes.size() > cont) {
                        solution[i] = 1;
                    }
                }

            }

    }

    public boolean validateSolution(Integer[]solution, ArrayList<Comuna>comunas)
    {
        int cont;
        boolean flag;
        //VALIDAR QUE TODAS LAS ANTENAS ESTÁN ENTRELAZADAS
        for(int i=0; i<comunas.size();i++)
        {
            Comuna currentCommune = comunas.get(i);
            if(solution[1]==1){

                for (int j=0; j<currentCommune.comunasColindantes.size(); j++)
                {
                    flag=false;

                    for(int k=0; k<comunas.size();k++)
                    {
                        if(comunas.get(k).id==currentCommune.comunasColindantes.get(j))
                        {
                            if(solution[k]==1)
                            {
                                flag=true;
                            }
                            break;
                        }
                    }
                    if(!flag)
                    {
                        return flag;
                    }
                }
            }else{
                cont=0;
                for(int j=0; j<currentCommune.comunasColindantes.size();j++)
                {
                    for (int k=0; k<comunas.size();k++)
                    {
                        if(comunas.get(k).id==currentCommune.comunasColindantes.get(j))
                        {
                            if(solution[k]==1)
                            {
                                cont++;
                            }
                            break;
                        }
                    }
                }
                if(cont==0)
                {
                    return false;
                }
            }

        }
        return true;
    }

    public double valueCost(Integer[]solution, ArrayList<Comuna>comunas){
        double newCost=-1;
        for(int lenghtSolution = 0; lenghtSolution < solution.length ; lenghtSolution++){
            if(solution[lenghtSolution]==1){
                newCost+=comunas.get(lenghtSolution).costo;
            }
        }
        return newCost;
    }

}