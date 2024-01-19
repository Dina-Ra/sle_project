package ru.matrix.creator.util;

import ru.matrix.creator.MatrixDetailCreatorException;

import java.util.Arrays;

public class MatrixLUUtils {
    private MatrixLUUtils() {
    }

    public static MatrixLU getMatrixLU(double[][] a, double[] b) throws MatrixDetailCreatorException {
        double[][] l = new double[a.length][a.length]; // в матрице l[][] диагональным значениям присвоить 1.0
        for (int i = 0; i < l.length; i++) {
            l[i][i] = 1.0;
        }

        //
        double[][] u = new double[a.length][a.length]; // в матрицу u[][] копируем значения a[][]
        for (int i = 0; i < u.length; i++) {
            u[i] = Arrays.copyOf(a[i], a[i].length);
        }

        int count = 0; // число перестановок строк
        try {
            for (int k = 0; k < u.length - 1; k++) {
                // если первый элемент строки равен нулю, проводим поиск строки с ненулевым первым элементом, переставляем строки
                if (u[k][k] == 0) {
                    for (int n = k + 1; k < u.length; k++) {
                        if (u[k][n] != 0.0) {
                            double[] copy = u[n];
                            u[n] = u[k];
                            u[k] = copy;

                            double copy_b = b[n];
                            b[n] = b[k];
                            b[k] = copy_b;

                            count++;
                            break;
                        }
                    }
                }

                if (u[k][k] == 0) {
                    break;
                } else {
                    for (int i = k + 1; i < l.length; i++) {
                        double el = u[i][k] / u[k][k];
                        l[i][k] = el;
                    }

                    for (int i = k + 1; i < u.length; i++) {
                        for (int j = k; j < u.length; j++) {
                            u[i][j] = u[i][j] - l[i][k] * u[k][j];
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new MatrixDetailCreatorException(ex.getMessage());
        }

        return new MatrixLU(l, u, count == 0 ? null : b, count);
    }
}
