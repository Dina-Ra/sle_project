package ru.matrix.creator.util;

import ru.matrix.creator.MatrixDetailCreatorException;

public class MatrixDecompositionUtils {
    private MatrixDecompositionUtils() {
    }

    public static double[] getDecomposition(double[][] l, double[][] u, double[] b) throws MatrixDetailCreatorException {
        try {
            /*
                1 этап - решить Lz = b;
            */

            double[] z = new double[b.length];
            for (int i = 0; i < l.length; i++) {
                z[i] = b[i];
                for (int j = i - 1; j >= 0; j--) {
                    z[i] = z[i] - l[i][j] * z[j];
                }
            }

            /*
                2 этап - решить Ux = z
             */
            double[] x = new double[b.length];
            for (int i = l.length - 1; i >= 0; i--) {
                double each = 0.0;
                for (int j = l.length - 1; j > i; j--) {
                    each = each + x[j] * u[i][j];
                }
                x[i] = (z[i] - each) / u[i][i];
            }

            return x;
        } catch (Exception ex) {
            throw new MatrixDetailCreatorException(ex.getMessage());
        }
    }
}
