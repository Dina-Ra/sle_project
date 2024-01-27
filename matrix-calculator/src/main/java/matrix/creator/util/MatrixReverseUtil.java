package matrix.creator.util;

import matrix.creator.MatrixDetailCreatorException;

public class MatrixReverseUtil {
    private MatrixReverseUtil() {
    }

    public static double[][] getMatrixReverse(double[][] u, double[][] l) throws MatrixDetailCreatorException {
        // единичная матрица
        double[][] e = new double[u.length][u.length];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e.length; j++) {
                if (i == j) {
                    e[i][j] = 1;
                } else {
                    e[i][j] = 0;
                }
            }
        }

        double[][] a_ = new double[u.length][u.length];

        try {
            // прямой ход
            for (int k = 0; k < e.length - 1; k++) {
                for (int i = k + 1; i < e.length; i++) {
                    for (int j = 0; j < e.length; j++) {
                        e[i][j] = e[i][j] - e[k][j] * l[i][k];
                    }
                }
            }

            // обратный ход
            for (int k = 0; k < a_.length; k++) {
                for (int i = a_.length - 1; i >= 0; i--) {
                    double each = 0.0;
                    for (int j = a_.length - 1; j > i; j--) {
                        each = each + a_[j][k] * u[i][j];
                    }
                    a_[i][k] = (double) Math.round(((e[i][k] - each) / u[i][i]) * 1000) / 1000; // производим окрегление вещественного числа
                }
            }
        } catch (Exception ex) {
            throw new MatrixDetailCreatorException(ex.getMessage());
        }
        return a_;
    }
}
