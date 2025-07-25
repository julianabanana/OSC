import java.util.Scanner;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinMergesort<T extends Comparable<T>> extends RecursiveAction {
    private static final int UMBRAL = 100;

    private final T[] arreglo;
    private final T[] auxiliar;
    private final int izquierda;
    private final int derecha;

    public ForkJoinMergesort(T[] arreglo, T[] auxiliar, int izquierda, int derecha) {
        this.arreglo = arreglo;
        this.auxiliar = auxiliar;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    protected void compute() {
        if (derecha - izquierda < UMBRAL) {
            ordenarPorInsercion(arreglo, izquierda, derecha);
        } else {
            int medio = (izquierda + derecha) / 2;

            ForkJoinMergesort<T> tareaIzquierda = new ForkJoinMergesort<>(arreglo, auxiliar, izquierda, medio);
            ForkJoinMergesort<T> tareaDerecha = new ForkJoinMergesort<>(arreglo, auxiliar, medio + 1, derecha);

            invokeAll(tareaIzquierda, tareaDerecha);

            fusionar(arreglo, auxiliar, izquierda, medio, derecha);
        }
    }

    private void fusionar(T[] arr, T[] aux, int izquierda, int medio, int derecha) {
        // Copiar al arreglo auxiliar
        for (int i = izquierda; i <= derecha; i++) {
            aux[i] = arr[i];
        }

        int i = izquierda;
        int j = medio + 1;
        int k = izquierda;

        while (i <= medio && j <= derecha) {
            if (aux[i].compareTo(aux[j]) <= 0) {
                arr[k++] = aux[i++];
            } else {
                arr[k++] = aux[j++];
            }
        }

        while (i <= medio) {
            arr[k++] = aux[i++];
        }
        // No se necesita copiar la mitad derecha restante, ya está en su lugar
    }

    private void ordenarPorInsercion(T[] arr, int bajo, int alto) {
        for (int i = bajo + 1; i <= alto; i++) {
            T clave = arr[i];
            int j = i - 1;
            while (j >= bajo && arr[j].compareTo(clave) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = clave;
        }
    }

    // Método main para probar el algoritmo pidiendo datos al usuario
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¿Cuántos elementos deseas ordenar?");
        int n = scanner.nextInt();

        Integer[] datos = new Integer[n];
        Integer[] auxiliar = new Integer[n];

        System.out.println("Ingresa " + n + " números enteros:");
        for (int i = 0; i < n; i++) {
            datos[i] = scanner.nextInt();
        }

        ForkJoinMergesort<Integer> tarea = new ForkJoinMergesort<>(datos, auxiliar, 0, datos.length - 1);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(tarea);

        System.out.print("Arreglo ordenado: ");
        for (int num : datos) {
            System.out.print(num + " ");
        }

        scanner.close();
    }
}
