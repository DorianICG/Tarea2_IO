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


/**
 *      MÉTODO LEER COMUNAS: SE ENCARGA DE LEER LAS COMUNAS DE UN CSV .
 * */
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


 /**
  *     MÉTODO GENERATE FIRST SOLUTION: genera la primera solución (100 soluciones).
  * */
    public void generateFirstSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        while(solutions.size()<100)                             // 100 SOLUCIONES INFACTIBLES
        {
            Integer[]solution = new Integer[comunas.size()];    // INICIALIZAR LA SOLUCIÓN
            solutions.add(newSolution(solution, comunas));      // AÑADIR UNA SOLUCIÓN
        }
    }


/**
 *      MÉTODO NEW SOLUTION: genera una solución de manera randómica, pero en donde la probabilidad
 *      de que se asigne un 0 al arreglo de solución es mayor a la probabilidad de que se asigne
 *      un 1 a la solución. Luego de eso hace un llamado a un método que hace que hace válida
 *      la solución creada.
 * */
    public Integer[] newSolution(Integer[]solution, ArrayList<Comuna>comunas){
        Random rn = new Random();                       // INICIAR RN TIPO RANDOM
        double alfa = 0.80;                              // DAR UNA PROBABILIDAD ALFA
        for (int i = 0; i < solution.length; i++)       // RECORRER EL TAMAÑO DEL ARREGLO
        {
            double beta = rn.nextDouble();
            if(beta>alfa){                              //SI EL NÚMERO SACADO RANDÓMICAMENTE ES MENOR A ALFA
                solution[i] = 1;                        // ES 1 SI ES MENOR
            }else{
                solution[i] = 0;                        // 0 SI NO LO ES
            }
        }
        validSolution(solution,comunas);                // TRANSFORMAR LA SOLUCIÓN A ÓPTIMA
        return solution;                                // RETORNAR LA SOLUCIÓN ÓPTIMA
    }


 /**
  *     MÉTODO BEST SOLUTION: elige dentro del arreglo de soluciones a la solución más factible.
  * */
    public Integer[] bestSolution(ArrayList<Integer[]>solutions,ArrayList<Comuna>comunas) {
        Integer[] firstSolution = null;                 // INICIAR LA SELECCIÓN DE SOLUCIÓN COMO NULL
        double firstCoste = 0;                          // INICIAR EL MEJOR COSTE EN 0
        int i=0;                                        // INICIAR I EN 0
        for(Integer[]solution: solutions)               // RECORRER EL ARREGLO DE SOLUCIONES
        {
            if(i==0)                                    // SI i ES 0, LA MEJOR SOLUCIÓN ES LA PRIMERA SOLUCIÓN GUARDADA
            {
                firstSolution = solution;               // PRIMERA SOLUCIÓN ES IGUAL A LA PRIMERA SOLUCIÓN
                firstCoste = valueCost(solution, comunas);  // PRIMER COSTE ES EL COSTE DE LA PRIMERA SOLUCIÓN
                i++;                                        // I+1
            } else if (valueCost(solution, comunas)<firstCoste) {   // SI OTRA SOLUCIÓN ES MEJOR A NUESTRA MEJOR SOLUCIÓN
                firstSolution = solution;                           // REMPLAZAR
                firstCoste = valueCost(solution, comunas);          // REMPLAZAR
            }
        }
        return firstSolution;                                       // RETORNAR LA MEJOR SOLUCIÓN
    }


/**
 *      MÉTODO SELECT PARENTS TO CROSS: elige de manera elitista a las mejores N soluciones dentro del arreglo
 *      de soluciones.
 **/

    public ArrayList<Integer[]>selectParentsToCross(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas, int sampleSize) {
        ArrayList<Integer[]> parents = solutions;                   // COPIA A PADRES LAS SOLUCIONES
        bubbleSortInteger(parents,comunas);                         // ORDENA EL ARREGLO DE SOLUCIONES DE MEJOR A PEOR
        parents.subList(sampleSize,solutions.size()).clear();       // CORTA EL ARREGLO DE PADRES A UN TAMAÑO DETERMINADO
        return parents;                                             // RETORNAR LOS PADRES
    }


/**
 *      MÉTODO PARENTS RANDOM: elige dentro de las soluciones padres dos de ellos para
 *      luego retornar al hijo de la cruza.
 * */
    public Integer[] parentsRandom(ArrayList<Integer[]>parents, ArrayList<Comuna>comunas) {
        Integer[] father;                                                   // INSTANCIAR FATHER
        Integer[] mother;                                                   // INSTANCIAR MOTHER
        int cont=0;                                                         // INSTANCIAR CONTADOR
        Random rn = new Random();                                           // INSTANCIAR RANDOM
        father=parents.get(rn.nextInt(parents.size()));                     // SACAR A UN PADRE DE MANERA ALEATORIA Y LO ALMACENAMOS EN FATHER
        do{                                                                 // DO-WHILE MIENTRAS LOS PADRES SEAN IGUALES O CONTADOR SEA MENOR A 50
            mother=parents.get(rn.nextInt(parents.size()));                 // SACAR UNA MADRE DE MANERA ALEATORIA Y LO ALMACENAMOS EN MOTHER
            cont++;                                                         // AUMENTAR CONTADOR PARA NO QUEDAR EN UN BUCLE EN CASO DE QUE TOME UN MISMO PADRE 100 VECES
        }while(father.equals(mother)&&cont<100);                            // DO-WHILE FIN
        return crossParents(father,mother,comunas);                         // RETORNAR LA CRUZA DE LOS PADRES.
    }


/**
 *      MÉTODO CROSS PARENTS: cruza a los padres en un punto elegido aleatoriamente
 *      para luego retornar el mejor hijo de ambos.
 * */
    public Integer[] crossParents(Integer[] father, Integer[] mother, ArrayList<Comuna>comunas) {
        ArrayList<Integer[]>sons = new ArrayList<>(2);           // CREAR UN ARREGLO DE HIJOS DE TAMAÑO 2 QUE VA A SER LA MEZCLA DE LOS DOS PADRES
        Random rn = new Random();                                            // INSTANCIAMOS RANDOM
        int random =  rn.nextInt(father.length);                             // RANDOM SERÁ UN NÚMERO ALEATORIO PARA CRUZAR LOS PADRES
        int temp;                                                            // VARIABLE TEMPORAL
        Integer[] copyF, copyM;                                              // VARIABLES PARA COPIAR AL PADRE Y LA MADRE
        copyF=father;                                                        // COPIA AL PADRE EN COPYF
        copyM=mother;                                                        // COPIA A LA MADRE EN COPYM
        for(int j=random; j< father.length;j++)                              // VA DESDE EL NÚMERO ALEATORIO HASTA EL TAMAÑO DE LA SOLUCIÓN
        {
            temp = copyF[j];                                                 // PERMUTA LA POSICIÓN
            copyF[j]=copyM[j];                                               // DEL PADRE A LA MADRE
            copyM[j]=temp;                                                   // Y VICEVERSA
        }
        sons.add(copyF);                                                     // AGREGAR AL PADRE CRUZADO CON LA MADRE
        sons.add(copyM);                                                     // AGREGAR LA MADRE CRUZADO CON EL PADRE
        return bestSolution(sons,comunas);                                   // RETORNAR EL MEJOR
    }



/**
 *      MÉTODO MUTATE SON: muta al hijo volviendo a hacer a el hijo una solución válida.
 *
 *      TODOS LOS HIJOS MUTAN.
 * */
    public void mutateSon(Integer[]son, ArrayList<Comuna>comunas) {
        validSolution(son, comunas);                    // CONVERTIR A LOS HIJOS NUEVAMENTE EN UNA SOLUCIÓN FACTIBLE
    }


/**
 *      METODO GET BETTER SOLUTIONS: toma a las mejores N soluciones dentro de todas las soluciones
 *      que se han generado y las que ya habían sido generadas.
 * */
    public void getBetterSolutions(ArrayList<Integer[]>solutions,ArrayList<Integer[]>solutionsNewGenaration,ArrayList<Comuna>comunas, int sampleSize) {
        solutions.addAll(solutionsNewGenaration);                           // AGREGA LA NUEVA GENERACIÓN DE SOLUCIONES EN DE SOLUCIONES
        bubbleSortInteger(solutions, comunas);                              // ORDENAR DE MENOR A MAYOR LAS SOLUCIONES
        solutions.subList(sampleSize,solutions.size()).clear();             // REALIZAR EL CORTE AL TAMAÑO QUE LE ASIGNARON
    }


/**
 *      MÉTODO BUBBLE SORT INTEGER: ordena un arreglo de Integer[] donde el que
 *      tiene menor costo va primero y el que tiene mayor costo de instalación va al
 *      último
 * */
    public void bubbleSortInteger(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        for(int i=0; i<solutions.size()-1;i++)                                                  // APLICAR BUBBLE SORT PARA ORDENAR EL ARREGLO
        {
            for(int j=0; j<solutions.size()-1-i;j++)                                            // VALIDAR QUÉ SOLUCIONES SON MÁS FACTIBLES Y COLOCANDOLAS AL PRINCIPIO
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


/**
 *      MÉTODO GET BEST SOLUTION: obtiene la mejor solución dentro del mundo de soluciones
 * */
    public Integer[] getBestSolution(ArrayList<Integer[]>solutions, ArrayList<Comuna>comunas) {
        Integer[] bestSolutionArray =null;                                                              // INICIAR UN INTEGER[] PARA ALMACENAR LA MEJOR SOLUCIÓN
        double bestCost=0;                                                                              // INICIAR BESTCOST PARA ALMACENAR EL MEJOR COSTO
        for(int i=0;i<solutions.size(); i++)                                                            // RECORRER TODO EL ARREGLO DE SOLUCIONES
        {
            if(i==0)                                                                                    // VALIDAR SI ES EL PRIMER DATO
            {
                bestSolutionArray = solutions.get(i);                                                   // GUARDAR MEJOR SOLUCIÓN
                bestCost = valueCost(solutions.get(i), comunas);                                        // GUARDAR EL COSTE
            }else if(valueCost(solutions.get(i), comunas)<bestCost)                                     // SI EL COSTE SACADO ES MEJOR AL QUE YA ESTÁ
            {
                bestSolutionArray = solutions.get(i);                                                   // GUARDAR EN MEJOR SOLUCIÓN
                bestCost = valueCost(solutions.get(i), comunas);                                        // GUARDAR EN MEJOR COSTE
            }
        }
        return bestSolutionArray;                                                                       // RETORNAR LA MEJOR SOLUCIÓN
    }



/**
 *     MÉTODO VALID SOLUTION: valida que la solución entregada es una factible o no,
 *     en caso de que no la sea, éste método la corrige y la convierte nuevamente en una solución factible.
 * */
    public void validSolution(Integer[]solution, ArrayList<Comuna>comunas) {
        int cont, mejorPos, number;                                             // INICAMOS UN CONTADOR, MEJOR POSICION Y NUMERO
        double mejorCoste, costeNuev;                                           // INCIAMOS EL MEJOR COSTE Y EL COSTE NUEVO
        Random rn = new Random();                                               // RN PARA CREAR NÚMEROS ALEATORIOS
        number = rn.nextInt(solution.length);                                   // ESCOGIMOS UN NÚMERO ALEATORIO
        for(int i=number; i<comunas.size();i++)                                 // CICLO FOR QUE VA DESDE LA MITAD HASTA EL FINAL DEL TAMAÑO DE COMUNAS
        {
            if(solution[i]==0){                                                     // VERIFICA SI EN LA POSICIÓN DE LA SOLUCIÓN HAY UN 1 O NO
                cont=0;                                                             // CONTADOR IGUAL A 0
                for(int j=0; j<comunas.get(i).comunasColindantes.size();j++)        // CICLO FOR PARA RECORRER TODAS LAS COMUNAS COLINDANTES DE LA COMUNA
                {
                    for(int k = 0; k<comunas.size();k++)                            // RECORRER NUEVAMENTE NUESTRO ARREGLO DE COMUNAS PARA ENCONTRAR LA COMUNA VECINA POR SU ID
                    {
                        if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))     // VER SI LA ID DE LA COMUNA COLINDANTE ES IGUAL A LA ID DE LA COMUNA[k]
                        {
                            if(solution[k]==1)                                              // VER SI LA COMUNA VECINA TIENE UN 1 (ANTENA) EN LA MISMA POSICIÓN DEL ARREGLO SOLUCIÓN
                            {
                                cont++;                                                     // SUMAR EL CONTADOR EN 1
                                break;                                                      // BREAK PARA SALIR DEL FOR E IR CON LA SIGUIENTE ITERACIÓN
                            }
                        }
                    }
                    if (cont>0) break;                                                      // BREAR PARA SALIR E IR CON LA SIGUIENTE COMUNA
                }
                if(cont==0)                                                                 // SI NO HAY COMUNAS CON ANTENAS DENTRO DE SU RANGO
                {
                    mejorCoste = comunas.get(i).costo / comunas.get(i).comunasColindantes.size();   //  ASIGNAR A MEJOR COSTE A LA COMUNA PRINCIPAL [i]
                    mejorPos = i;                                                                   //  GUARDAR LA POSICIÓN DE LA COMUNA PRINCIPAL [i]
                    for(int j=0;j<comunas.get(i).comunasColindantes.size();j++)                     //  RECORRER LAS COMUNAS COLINDANTES DE LA COMUNA PRINCIPAL
                    {
                        for(int k = 0; k<comunas.size();k++)                                        //  RECORRER EL ARREGLO DE COMUNAS
                        {
                            if(comunas.get(k).id==comunas.get(i).comunasColindantes.get(j))         //  VALIDAR QUE LA ID DE LA COMUNA COINCIDE CON LA DE LA COMUNA COLINDANTE
                            {
                                costeNuev = comunas.get(k).costo / comunas.get(k).comunasColindantes.size();    //  SACAR EL COSTE NUEVO
                                if(costeNuev < mejorCoste)                                                      //  SI EL COSTE NUEVO ES MEJOR AL MEJOR COSTE QUE HAY
                                {
                                    mejorCoste = costeNuev;                                                     //  REMPLAZA AL MEJOR COSTE
                                    mejorPos = k;                                                               //  REMPLAZA LA POSICIÓN

                                }
                                break;                                                                          // SALIR DEL FOR DE COMUNAS[K]
                            }
                        }
                    }
                    solution[mejorPos]=1;                                                                       // SOLUCIÓN[MEJOR POSICIÓN] SERA 1
                }
            }
        }

        for(int i=number; i>-1;i--)                             // RECORRE A LA PARTE FALTANTE DE EL ARREGLO DE SOLUCIONES
        {
            if(solution[i]==0){
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
                    if(cont>0) break;
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


    }


/**
 *      MÉTODO VALIDATE SOLUTION: valida que la solución creada esté correcta.
 * */
    public boolean validateSolution(Integer[]solution, ArrayList<Comuna>comunas) {
        int cont;
        //VALIDAR QUE TODAS LAS COMUNAS TENGAN COBERTURA
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
                    if(cont>0) break;
                }
                if(cont==0)
                {
                    return false;
                }
            }

        }
        return true;
    }



/**
 *      MÉTODO VALUE COST: calcula el costo de tener las antenas en esas posiciones.
 * */
    public double valueCost(Integer[]solution, ArrayList<Comuna>comunas){
        double newCost=0;                                                                   // NUEVO COSTO EN 0
        for(int lenghtSolution = 0; lenghtSolution < solution.length ; lenghtSolution++){   // RECORRER LA SOLUCION
            if(solution[lenghtSolution]==1){                                                // SI TIENE ANTENA APLICAR SE SUMA AL COSTO TOTAL
                newCost+=comunas.get(lenghtSolution).costo;                                 // SE AGREGA AL COSTO
            }
        }
        return newCost;                                                                     // RETORNO EL COSTO TOTAL
    }

}