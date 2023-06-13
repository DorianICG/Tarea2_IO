package org.example;

import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.ArrayList;
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

    public void generateFirstSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        while(solutions.size()<100)                             // GENERO 100 SOLUCIONES INFACTIBLES
        {
            Integer[]solution = new Integer[comunas.size()];    // INICIALIZA LA SOLUCIÓN
            solutions.add(newSolution(solution, comunas));      // AÑADE UNA SOLUCIÓN
        }
    }

    public Integer[] newSolution(Integer[]solution, ArrayList<Comuna>comunas){
        Random rn = new Random();                       // INICIALIZO RN TIPO RANDOM
        double alfa = 0.66;                              // DAMOS UNA PROBABILIDAD ALFA
        for (int i = 0; i < solution.length; i++)       // RECORREMOS EL TAMAÑO DE NUESTRO ARREGLO
        {
            double beta = rn.nextDouble();
            if(beta>alfa){                              // VEMOS SI EL NÚMERO SACADO RANDÓMICAMENTE ES MENOR A NUESTRO ALFA
                solution[i] = 1;                        // ES 1 SI ES MENOR
            }else{
                solution[i] = 0;                        // 0 SI NO LO ES
            }
        }
        validSolution(solution,comunas);                // TRANSFORMAR LA SOLUCIÓN A ÓPTIMA
        return solution;                                // RETORNO LA SOLUCIÓN ÓPTIMA
    }

    public Integer[] bestSolution(ArrayList<Integer[]>solutions,ArrayList<Comuna>comunas) {
        Integer[] firstSolution = null;                 // INICIAR LA SELECCIÓN DE SOLUCIÓN COMO NULL
        double firstCoste = 0;                          // INICIAR EL MEJOR COSTE EN 0
        int i=0;                                        // INICIAR I EN 0
        for(Integer[]solution: solutions)               // RECORRER NUESTRO ARREGLO DE SOLUCIONES
        {
            if(i==0)                                    // SI I ES 0, NUESTRA MEJOR SOLUCIÓN ES LA PRIMERA SOLUCIÓN GUARDADA
            {
                firstSolution = solution;               // PRIMERA SOLUCIÓN ES IGUAL A LA PRIMERA SOLUCIÓN
                firstCoste = valueCost(solution, comunas);  // PRIMER COSTE ES EL COSTE DE LA PRIMERA SOLUCIÓN
                i++;                                        // I+1
            } else if (valueCost(solution, comunas)<firstCoste) {   // VEMOS SI OTRA SOLUCIÓN ES MEJOR A NUESTRA MEJOR SOLUCIÓN
                firstSolution = solution;                           // REMPLAZAMOS
                firstCoste = valueCost(solution, comunas);          // REMPLAZAMOS
            }
        }
        return firstSolution;                                       // RETORNAMOS LA MEJOR SOLUCIÓN
    }

    public ArrayList<Integer[]>selectParentsToCross(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas, int sampleSize) {
        ArrayList<Integer[]> parents = solutions;                   // COPIAMOS A PADRES LAS SOLUCIONES
        bubbleSortInteger(parents,comunas);                         // ORDENAMOS NUESTRO ARREGLO DE SOLUCIONES DE MEJOR A PEOR
        parents.subList(sampleSize,solutions.size()).clear();       // CORTAMOS NUESTRO ARREGLO DE PADRES A UN TAMAÑO DETERMINADO
        return parents;                                             // RETORNAMOS LOS PADRES
    }

    public Integer[] parentsRandom(ArrayList<Integer[]>parents, ArrayList<Comuna>comunas) {
        Integer[] father;                                                   // INSTANCIAMOS FATHER
        Integer[] mother;                                                   // INSTANCIAMOS MOTHER
        int cont=0;                                                         // INSTANCIAMOS CONTADOR
        Random rn = new Random();                                           // INSTANCIAMOS RANDOM
        father=parents.get(rn.nextInt(parents.size()));                     // SACAMOS A UN PADRE DE MANERA ALEATORIA Y LO ALMACENAMOS EN FATHER
        do{                                                                 // DO-WHILE MIENTRAS LOS PADRES SEAN IGUALES O CONTADOR SEA MENOR A 50
            mother=parents.get(rn.nextInt(parents.size()));                 // SACAMOS UNA MADRE DE MANERA ALEATORIA Y LO ALMACENAMOS EN MOTHER
            cont++;                                                         // AUMENTAMOS CONTADOR PARA NO QUEDAR EN UN BUCLE EN CASO DE QUE TOME UN MISMO PADRE 100 VECES
        }while(father.equals(mother)&&cont<100);                            // DO-WHILE FIN
        return crossParents(father,mother,comunas);                         // RETORNAMOS LA CRUZA DE LOS PADRES.
    }

    public Integer[] crossParents(Integer[] father, Integer[] mother, ArrayList<Comuna>comunas) {
        ArrayList<Integer[]>sons = new ArrayList<>(2);           // CREAMOS UN ARREGLO DE HIJOS DE TAMAÑO 2 QUE VA A SER LA MEZCLA DE LOS DOS PADRES
        Random rn = new Random();                                            // INSTANCIAMO RANDOM
        int random =  rn.nextInt(father.length);                             // RANDOM SERÁ UN NÚMERO ALEATORIO PARA CRUZAR LOS PADRES
        int temp;                                                            // VARIABLE TEMPORAL
        Integer[] copyF, copyM;                                              // VARIABLES PARA COPIAR AL PADRE Y LA MADRE
        copyF=father;                                                        // COPIAMOS AL PADRE EN COPYF
        copyM=mother;                                                        // COPIAMOS A LA MADRE EN COPYM
        for(int j=random; j< father.length;j++)                              // VOY DESDE EL NÚMERO ALEATORIO HASTA EL TAMAÑO DE LA SOLUCIÓN
        {
            temp = copyF[j];                                                 // PERMUTO LA POSICIÓN
            copyF[j]=copyM[j];                                               // DEL PADRE A LA MADRE
            copyM[j]=temp;                                                   // Y VICEVERSA
        }
        sons.add(copyF);                                                     // AGREGO AL PADRE CRUZADO CON LA MADRE
        sons.add(copyM);                                                     // AGREGO LA MADRE CRUZADO CON EL PADRE
        return bestSolution(sons,comunas);                                   // RETORNAMOS EL MEJOR
    }

    public void mutateSon(Integer[]son, ArrayList<Comuna>comunas) {
        validSolution(son, comunas);                    // CONVIERTO A LOS HIJOS NUEVAMENTE EN UNA SOLUCIÓN FACTIBLE
    }

    public void getBetterSolutions(ArrayList<Integer[]>solutions,ArrayList<Integer[]>solutionsNewGenaration,ArrayList<Comuna>comunas, int sampleSize) {
        solutions.addAll(solutionsNewGenaration);                           // AGREGA LA NUEVA GENERACIÓN DE SOLUCIONES EN DE SOLUCIONES
        bubbleSortInteger(solutions, comunas);                              // ORDENAMOS DE MAYOR A MENOR LAS SOLUCIONES
        solutions.subList(sampleSize,solutions.size()).clear();             // REALIZAMOS EL CORTE AL TAMAÑO QUE LE ASIGNEMOS
    }

    public void bubbleSortInteger(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        for(int i=0; i<solutions.size()-1;i++)                                                  // APLICAMOS BUBBLE SORT PARA ORDENAR EL ARREGLO
        {
            for(int j=0; j<solutions.size()-1-i;j++)                                            // VALIDANDO QUE SOLUCIONES SON MÁS FACTIBLES Y COLOCANDOLAS AL PRINCIPIO
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

    public Integer[] getBestSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        Integer[] bestSolutionArray =null;                                                              // INICIAMOS UN INTEGER[] PARA ALMACENAR LA MEJOR SOLUCIÓN
        double bestCost=0;                                                                              // INICIAMOS BESTCOST PARA ALMACENAR EL MEJOR COSTO
        for(int i=0;i<solutions.size(); i++)                                                            // RECORRO TODO NUESTRO ARREGLO DE SOLUCIONES
        {
            if(i==0)                                                                                    // VÁLIDO SI ES EL PRIMER DATO
            {
                bestSolutionArray = solutions.get(i);                                                   // GUARDO MEJOR SOLUCIÓN
                bestCost = valueCost(solutions.get(i), comunas);                                        // GUARDO EL COSTE
            }else if(valueCost(solutions.get(i), comunas)<bestCost)                                     // VEO SI EL COSTE SACADO ES MEJOR AL QUE YA TENÍAMOS
            {
                bestSolutionArray = solutions.get(i);                                                   // GUARDO EN MEJOR SOLUCIÓN
                bestCost = valueCost(solutions.get(i), comunas);                                        // GUARDO EN MEJOR COSTE
            }
        }
        return bestSolutionArray;                                                                       // RETORNO LA MEJOR SOLUCIÓN
    }

    public void validSolution(Integer[]solution, ArrayList<Comuna>comunas) {
        int cont, mejorPos, number;
        double mejorCoste, costeNuev;
        Random rn = new Random();
        number = rn.nextInt(solution.length);
        for(int i=number; i<comunas.size();i++)
        {
            cont=0;
            for(int j=0; j<comunas.get(i).comunasColindantes.size();j++)
            {
                for(int k = 0; k<comunas.size();k++)
                {
                    if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))
                    {
                        if(solution[k]==1)
                        {
                            cont++;
                            break;
                        }
                    }
                }
            }
            if(cont==0)
            {
                mejorCoste = comunas.get(i).costo / comunas.get(i).comunasColindantes.size();
                mejorPos = i;
                for(int j=0;j<comunas.get(i).comunasColindantes.size();j++)
                {
                    for(int k = 0; k<comunas.size();k++)
                    {
                        if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))
                        {
                            costeNuev = comunas.get(k).costo / comunas.get(k).comunasColindantes.size();
                            if(costeNuev < mejorCoste)
                            {
                                mejorCoste = costeNuev;
                                mejorPos = k;

                            }
                            break;
                        }
                    }
                }
                solution[mejorPos]=1;
            }

        }

        for(int i=number; i>-1;i--)
        {
            cont=0;
            for(int j=0; j<comunas.get(i).comunasColindantes.size();j++)
            {
                for(int k = 0; k<comunas.size();k++)
                {
                    if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))
                    {
                        if(solution[k]==1)
                        {
                            cont++;
                            break;
                        }
                    }
                }
            }
            if(cont==0)
            {
                mejorCoste = comunas.get(i).costo / comunas.get(i).comunasColindantes.size();
                mejorPos = i;
                for(int j=0;j<comunas.get(i).comunasColindantes.size();j++)
                {
                    for(int k = 0; k<comunas.size();k++)
                    {
                        if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))
                        {
                            costeNuev = comunas.get(k).costo / comunas.get(k).comunasColindantes.size();
                            if(costeNuev < mejorCoste)
                            {
                                mejorCoste = costeNuev;
                                mejorPos = k;

                            }
                            break;
                        }
                    }
                }
                solution[mejorPos]=1;
            }

        }


    }

    public boolean validateSolution(Integer[]solution, ArrayList<Comuna>comunas) {
        int cont;
        boolean flag;
        //VALIDAR QUE TODAS LAS ANTENAS ESTÁN ENTRELAZADAS
        for(int i=0; i<comunas.size();i++)
        {
            Comuna currentCommune = comunas.get(i);
            if(solution[1]==0){
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