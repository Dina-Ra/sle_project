package ru.matrix.creator.util;

import ru.matrix.creator.MatrixDetailCreatorException;

public class MatrixTransposeUtils {
    private MatrixTransposeUtils() {
    }

    public static double[][] getMatrixTranspose(double[][] a) throws MatrixDetailCreatorException {
        try {
            double[][] a_t = new double[a.length][a.length];
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a.length; j++) {
                    if (i != j) {
                        a_t[j][i] = a[i][j];
                    } else {
                        a_t[i][i] = a[i][i];
                    }
                }
            }
            return a_t;
        } catch (Exception ex) {
            throw new MatrixDetailCreatorException(ex.getMessage());
        }
    }
}
