#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

int valid[11] = {0}; //array de resultados de cada hilo
int sudoku[9][9];

typedef struct {
    int fila;
    int columna;
} parameteros;

void read_sudoku(FILE* file) {
    for (int i = 0; i < 9; i++)
        for (int j = 0; j < 9; j++)
            fscanf(file, "%d", &sudoku[i][j]);
}

void *verColumna(void *param) {
    //iterar en cada columna 1-9
    for (int col = 0; col < 9; col++) {
        int digits[9] = {0};
        //por cada fila de la columna
        for (int fila = 0; fila < 9; fila++) {
            int num = sudoku[fila ][col];
            //si num esta fuera de rango o duplicado
            if (num < 1 || num > 9 || digits[num - 1] == 1) {
                valid[0] = 0; //columna invalida
                pthread_exit(NULL);
            }
            digits[num - 1] = 1;//columna valida
        }
    }
    valid[0] = 1; //todas las columnas son validas
    pthread_exit(NULL);
}

void *verFila(void *param) {
    //iterar en cada fila 1-9
    for (int fila = 0; fila < 9; fila++) {
        int digits[9] = {0};
        //por cada columna de la fila
        for (int col = 0; col < 9; col++) {
            int num = sudoku[fila][col];
            //si num esta fuera de rango o duplicado
            if (num < 1 || num > 9 || digits[num - 1] == 1) {
                valid[1] = 0; //fila invalida
                pthread_exit(NULL);
            }
            digits[num - 1] = 1;//fila valida
        }
    }
    valid[1] = 1; //todas las filas son validas
    pthread_exit(NULL);
}

void *verSubgrid(void *param) {
    //verSubgrid recibe de parametro la fila y columna de inicio
    parameteros *params = (parameteros *)param;
    int inFila = params->fila;
    int inColumna = params->columna;
    //cantidad de digitos en cada subcuadricula
    int digits[9] = {0};
    //por cada fila
    for (int fila = inFila; fila < inFila + 3; fila++) {
        //por cada columa de la fila
        for (int col = inColumna; col < inColumna + 3; col++) {
            int num = sudoku[fila][col];
            //si num esta fuera de rango o duplicado
            if (num < 1 || num > 9 || digits[num - 1] == 1) {
                int subCuad = 2 + (inFila / 3) * 3 + (inColumna / 3);
                valid[subCuad] = 0; //subcuadricula invalida
                pthread_exit(NULL);
            }
            digits[num - 1] = 1;//num valido
        }
    }
    valid[2 + (inFila / 3) * 3 + (inColumna / 3)] = 1;//subcuadricula valida
    pthread_exit(NULL);
}

int main(int argc, char* argv[]) {
    
    pthread_t threads[11];
    int thread_index = 0;
    
    FILE* file = fopen(argv[1], "r");
    read_sudoku(file);
    fclose(file);

    //Crear hilo para verificar columnas
    pthread_create(&threads[thread_index++], NULL, verColumna, NULL);

    //Crear hilo para verificar filas
    pthread_create(&threads[thread_index++], NULL, verFila, NULL);

    //9 hilos para verificar subcuadriculas
    for (int i = 0; i < 9; i += 3) {
        for (int j = 0; j < 9; j += 3) {
            parameteros *data = (parameteros *)malloc(sizeof(parameteros));
            data->fila = i;
            data->columna = j;
            //hilos de verificar subcuadricula con parametros de inicio
            pthread_create(&threads[thread_index++], NULL, verSubgrid, data);
        }
    }

    //Esperar a que los hilos terminen
    for (int i = 0; i < 11; i++) {
        pthread_join(threads[i], NULL);
    }

    //Verificar resultados
    int esValido = 1;
    for (int i = 0; i < 11; i++) {
        if (valid[i] == 0) {
            esValido = 0;
            break;
        }
    }

    if (esValido) {
        printf("La solución es válida\n");
    } else {
        printf("La solución no es válida\n");
    }

    return 0;
}