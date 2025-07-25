import java.util.Scanner;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

public class ForkJoinQuicksort<T extends Comparable<T>> extends RecursiveAction {
    private static final int UMBRAL = 100; // Umbral para usar Insertion Sort

    private final T[] arreglo;
    private final int izquierda;
    private final int derecha;

    public ForkJoinQuicksort(T[] arreglo, int izquierda, int derecha) {
        this.arreglo = arreglo;
        this.izquierda = izquierda;
        this.derecha = derecha;
    }

    @Override
    protected void compute() {
        if (derecha - izquierda < UMBRAL) {
            ordenarPorInsercion(arreglo, izquierda, derecha);
        } else {
            int indicePivote = particionar(arreglo, izquierda, derecha);
            ForkJoinQuicksort<T> tareaIzquierda = new ForkJoinQuicksort<>(arreglo, izquierda, indicePivote - 1);
            ForkJoinQuicksort<T> tareaDerecha = new ForkJoinQuicksort<>(arreglo, indicePivote + 1, derecha);
            invokeAll(tareaIzquierda, tareaDerecha);
        }
    }

    private int particionar(T[] arr, int bajo, int alto) {
        T pivote = arr[alto];
        int i = bajo - 1;
        for (int j = bajo; j < alto; j++) {
            if (arr[j].compareTo(pivote) <= 0) {
                i++;
                intercambiar(arr, i, j);
            }
        }
        intercambiar(arr, i + 1, alto);
        return i + 1;
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

    private void intercambiar(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Método main para probar el algoritmo pidiendo datos al usuario
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("¿Cuántos elementos deseas ordenar?");
        int n = scanner.nextInt();

        Integer[] datos = new Integer[n];

        System.out.println("Ingresa " + n + " números enteros:");
        for (int i = 0; i < n; i++) {
            datos[i] = scanner.nextInt();
        }

        // Crear tarea y ejecutarla en un pool ForkJoin
        ForkJoinQuicksort<Integer> tarea = new ForkJoinQuicksort<>(datos, 0, datos.length - 1);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(tarea);

        // Mostrar resultado
        System.out.print("Arreglo ordenado: ");
        for (int num : datos) {
            System.out.print(num + " ");
        }

        scanner.close();
    }
}
